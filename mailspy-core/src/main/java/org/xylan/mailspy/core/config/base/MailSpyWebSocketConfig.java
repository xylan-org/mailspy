/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.core.config.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpLogging;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.xylan.mailspy.core.config.MailSpyProperties;

@Configuration(proxyBeanMethods = false)
public class MailSpyWebSocketConfig {

    public static final String BROKER_DESTINATION_PREFIX = "/ws/topic";
    public static final String USER_DESTINATION_PREFIX = "/ws/topic/user";
    public static final String APPLICATION_DESTINATION_PREFIX = "/ws/dest";

    private MessageBrokerRegistry brokerRegistry;

    @Autowired
    private MailSpyProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ObjectMapper mailSpyObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public HandlerMapping mailSpyStompWebSocketHandlerMapping(
            @Qualifier("mailSpySubProtocolWebSocketHandler") WebSocketHandler subProtocolWebSocketHandler,
            @Qualifier("mailSpyMessageBrokerTaskScheduler") TaskScheduler taskScheduler) {
        WebSocketTransportRegistration transportRegistration = new WebSocketTransportRegistration();
        transportRegistration.setMessageSizeLimit(properties.getWebsocket().getMaxMessageBytes());
        transportRegistration.setSendBufferSizeLimit(properties.getWebsocket().getMaxSendBufferBytes());

        MailSpyWebMvcStompEndpointRegistry registry = new MailSpyWebMvcStompEndpointRegistry(
                subProtocolWebSocketHandler, transportRegistration, taskScheduler);
        if (applicationContext != null) {
            registry.setApplicationContext(applicationContext);
        }
        StompWebSocketEndpointRegistration endpointRegistration =
                registry.addEndpoint(properties.getPathNoTrailingSlash() + "/ws");
        if (properties.isEnableCors()) {
            endpointRegistration.setAllowedOriginPatterns("*");
        }
        endpointRegistration.withSockJS();
        return registry.getHandlerMapping();
    }

    @Bean
    public WebSocketHandler mailSpySubProtocolWebSocketHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel) {
        return new SubProtocolWebSocketHandler(inboundChannel, outboundChannel);
    }

    @Bean
    public WebSocketMessageBrokerStats mailSpyWebSocketMessageBrokerStats(
            @Qualifier("mailSpyStompBrokerRelayMessageHandler") @Nullable
                    AbstractBrokerMessageHandler stompBrokerRelayMessageHandler,
            @Qualifier("mailSpySubProtocolWebSocketHandler") WebSocketHandler subProtocolWebSocketHandler,
            @Qualifier("mailSpyClientInboundChannelExecutor") TaskExecutor inboundExecutor,
            @Qualifier("mailSpyClientOutboundChannelExecutor") TaskExecutor outboundExecutor,
            @Qualifier("mailSpyMessageBrokerTaskScheduler") TaskScheduler scheduler) {
        WebSocketMessageBrokerStats stats = new WebSocketMessageBrokerStats();
        stats.setSubProtocolWebSocketHandler((SubProtocolWebSocketHandler) subProtocolWebSocketHandler);
        if (stompBrokerRelayMessageHandler instanceof StompBrokerRelayMessageHandler) {
            stats.setStompBrokerRelay((StompBrokerRelayMessageHandler) stompBrokerRelayMessageHandler);
        }
        stats.setInboundChannelExecutor(inboundExecutor);
        stats.setOutboundChannelExecutor(outboundExecutor);
        stats.setSockJsTaskScheduler(scheduler);
        return stats;
    }

    @Bean
    public AbstractSubscribableChannel mailSpyClientInboundChannel(
            @Qualifier("mailSpyClientInboundChannelExecutor") TaskExecutor executor) {
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel(executor);
        channel.setLogger(SimpLogging.forLog(channel.getLogger()));
        channel.setInterceptors(List.of(new ImmutableMessageChannelInterceptor()));
        return channel;
    }

    @Bean
    public TaskExecutor mailSpyClientInboundChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("mailSpyWsClientInboundChannel-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean
    public AbstractSubscribableChannel mailSpyClientOutboundChannel(
            @Qualifier("mailSpyClientOutboundChannelExecutor") TaskExecutor executor) {
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel(executor);
        channel.setLogger(SimpLogging.forLog(channel.getLogger()));
        channel.setInterceptors(List.of(new ImmutableMessageChannelInterceptor()));
        return channel;
    }

    @Bean
    public TaskExecutor mailSpyClientOutboundChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("mailSpyWsClientOutboundChannel-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean
    public AbstractSubscribableChannel mailSpyBrokerChannel(
            @Qualifier("mailSpyBrokerChannelExecutor") TaskExecutor executor) {
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel(executor);
        channel.setLogger(SimpLogging.forLog(channel.getLogger()));
        channel.setInterceptors(List.of(new ImmutableMessageChannelInterceptor()));
        return channel;
    }

    @Bean
    public TaskExecutor mailSpyBrokerChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("mailSpyWsBrokerChannel-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean
    public AbstractBrokerMessageHandler mailSpySimpleBrokerMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyUserDestinationResolver") UserDestinationResolver userDestinationResolver) {
        SimpleBrokerMessageHandler handler = new SimpleBrokerMessageHandler(
                inboundChannel, outboundChannel, brokerChannel, List.of(BROKER_DESTINATION_PREFIX));
        handler.setSelectorHeaderName("selector");
        handler.setPreservePublishOrder(true);
        this.updateUserDestinationResolver(handler, userDestinationResolver);
        return handler;
    }

    private void updateUserDestinationResolver(
            AbstractBrokerMessageHandler handler, UserDestinationResolver userDestinationResolver) {
        Collection<String> prefixes = handler.getDestinationPrefixes();
        if (!prefixes.isEmpty() && !prefixes.iterator().next().startsWith("/")) {
            ((DefaultUserDestinationResolver) userDestinationResolver).setRemoveLeadingSlash(true);
        }
        handler.setUserDestinationPredicate(destination -> destination.startsWith(USER_DESTINATION_PREFIX));
    }

    @Bean
    public UserDestinationMessageHandler mailSpyUserDestinationMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel clientInboundChannel,
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyUserDestinationResolver") UserDestinationResolver userDestinationResolver) {
        return new UserDestinationMessageHandler(clientInboundChannel, brokerChannel, userDestinationResolver);
    }

    @Bean
    public TaskScheduler mailSpyMessageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("MailSpyWsMessageBroker-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Bean
    public SimpMessagingTemplate mailSpyBrokerMessagingTemplate(
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyBrokerMessageConverter") CompositeMessageConverter brokerMessageConverter) {
        SimpMessagingTemplate template = new SimpMessagingTemplate(brokerChannel);
        template.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
        template.setMessageConverter(brokerMessageConverter);
        return template;
    }

    @Bean
    @Primary
    public SimpMessagingTemplate primaryBrokerMessagingTemplate(
            @Qualifier("brokerMessagingTemplate") @Autowired(required = false)
                    SimpMessagingTemplate brokerMessagingTemplate) {
        return brokerMessagingTemplate;
    }

    @Bean
    public CompositeMessageConverter mailSpyBrokerMessageConverter(
            @Qualifier("mailSpyObjectMapper") ObjectMapper objectMapper) {
        List<MessageConverter> converters = new ArrayList<>();
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setContentTypeResolver(resolver);
        converters.add(new StringMessageConverter());
        converters.add(new ByteArrayMessageConverter());
        converters.add(converter);
        return new CompositeMessageConverter(converters);
    }

    @Bean
    public UserDestinationResolver mailSpyUserDestinationResolver(
            @Qualifier("mailSpyUserRegistry") SimpUserRegistry userRegistry) {
        DefaultUserDestinationResolver resolver = new DefaultUserDestinationResolver(userRegistry);
        resolver.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
        return resolver;
    }

    @Bean
    public SimpUserRegistry mailSpyUserRegistry() {
        return new DefaultSimpUserRegistry();
    }

    private static class MailSpyWebMvcStompEndpointRegistry extends WebMvcStompEndpointRegistry {
        public MailSpyWebMvcStompEndpointRegistry(
                WebSocketHandler webSocketHandler,
                WebSocketTransportRegistration transportRegistration,
                TaskScheduler defaultSockJsTaskScheduler) {
            super(webSocketHandler, transportRegistration, defaultSockJsTaskScheduler);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            super.setApplicationContext(applicationContext);
        }
    }
}
