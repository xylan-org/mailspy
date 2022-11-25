package org.xylan.mailspy.core.config.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.ws.DisabledSimpAnnotationMethodMessageHandler;

import java.util.List;

@Configuration
public class MailSpyWebSocketConfig extends WebSocketMessageBrokerConfigurationSupport {

    public static final String BROKER_DESTINATION_PREFIX = "/ws/topic";
    public static final String USER_DESTINATION_PREFIX = "/ws/topic/user";
    public static final String APPLICATION_DESTINATION_PREFIX = "/ws/dest";

    @Autowired
    private MailSpyProperties properties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX)
            .setUserDestinationPrefix(USER_DESTINATION_PREFIX)
            .setPreservePublishOrder(true)
            .enableSimpleBroker(BROKER_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        StompWebSocketEndpointRegistration endpointRegistration = registry.addEndpoint(properties.getPathNoTrailingSlash() + "/ws");
        if (properties.isEnableCors()) {
            endpointRegistration.setAllowedOriginPatterns("*");
        }
        endpointRegistration.withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(properties.getWebsocket().getMaxMessageBytes());
        registry.setSendBufferSizeLimit(properties.getWebsocket().getMaxSendBufferBytes());
    }

    @Override
    protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mailSpyObjectMapper());
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(new StringMessageConverter());
        messageConverters.add(new ByteArrayMessageConverter());
        messageConverters.add(converter);
        return false;
    }

    @Bean
    public ObjectMapper mailSpyObjectMapper() {
        return new ObjectMapper();
    }

    @Override
    @Bean("mailSpyStompWebSocketHandlerMapping")
    public HandlerMapping stompWebSocketHandlerMapping(
            @Qualifier("mailSpySubProtocolWebSocketHandler") WebSocketHandler subProtocolWebSocketHandler,
            @Qualifier("mailSpyMessageBrokerTaskScheduler") TaskScheduler taskScheduler) {
        return super.stompWebSocketHandlerMapping(subProtocolWebSocketHandler, taskScheduler);
    }

    @Override
    @Bean("mailSpySubProtocolWebSocketHandler")
    public WebSocketHandler subProtocolWebSocketHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel) {
        return super.subProtocolWebSocketHandler(inboundChannel, outboundChannel);
    }

    @Override
    @Bean("mailSpyWebSocketMessageBrokerStats")
    public WebSocketMessageBrokerStats webSocketMessageBrokerStats(
           @Qualifier("mailSpyStompBrokerRelayMessageHandler") @Nullable AbstractBrokerMessageHandler stompBrokerRelayMessageHandler,
           @Qualifier("mailSpySubProtocolWebSocketHandler") WebSocketHandler subProtocolWebSocketHandler,
           @Qualifier("mailSpyClientInboundChannelExecutor") TaskExecutor inboundExecutor,
           @Qualifier("mailSpyClientOutboundChannelExecutor") TaskExecutor outboundExecutor,
           @Qualifier("mailSpyMessageBrokerTaskScheduler") TaskScheduler scheduler) {
        return super.webSocketMessageBrokerStats(stompBrokerRelayMessageHandler, subProtocolWebSocketHandler,
                inboundExecutor, outboundExecutor, scheduler);
    }

    @Override
    @Bean("mailSpyClientInboundChannel")
    public AbstractSubscribableChannel clientInboundChannel(@Qualifier("mailSpyClientInboundChannelExecutor") TaskExecutor executor) {
        return super.clientInboundChannel(executor);
    }

    @Override
    @Bean("mailSpyClientInboundChannelExecutor")
    public TaskExecutor clientInboundChannelExecutor() {
        return super.clientInboundChannelExecutor();
    }

    @Override
    @Bean("mailSpyClientOutboundChannel")
    public AbstractSubscribableChannel clientOutboundChannel(@Qualifier("mailSpyClientOutboundChannelExecutor") TaskExecutor executor) {
        return super.clientOutboundChannel(executor);
    }

    @Override
    @Bean("mailSpyClientOutboundChannelExecutor")
    public TaskExecutor clientOutboundChannelExecutor() {
        return super.clientOutboundChannelExecutor();
    }

    @Override
    @Bean("mailSpyBrokerChannel")
    public AbstractSubscribableChannel brokerChannel(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerChannelExecutor") TaskExecutor executor) {
        return super.brokerChannel(inboundChannel, outboundChannel, executor);
    }

    @Override
    @Bean("mailSpyBrokerChannelExecutor")
    public TaskExecutor brokerChannelExecutor(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel) {
        return super.brokerChannelExecutor(inboundChannel, outboundChannel);
    }

    @Override
    @Bean("mailSpySimpAnnotationMethodMessageHandler")
    public SimpAnnotationMethodMessageHandler simpAnnotationMethodMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerMessagingTemplate") SimpMessagingTemplate brokerMessagingTemplate,
            @Qualifier("mailSpyBrokerMessageConverter") CompositeMessageConverter brokerMessageConverter) {
        return super.simpAnnotationMethodMessageHandler(inboundChannel, outboundChannel, brokerMessagingTemplate, brokerMessageConverter);
    }

    @Override
    protected SimpAnnotationMethodMessageHandler createAnnotationMethodMessageHandler(
            AbstractSubscribableChannel clientInboundChannel,
            AbstractSubscribableChannel clientOutboundChannel,
            SimpMessagingTemplate brokerMessagingTemplate) {
        return new DisabledSimpAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate);
    }

    @Override
    @Bean("mailSpySimpleBrokerMessageHandler")
    public AbstractBrokerMessageHandler simpleBrokerMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyUserDestinationResolver") UserDestinationResolver userDestinationResolver) {
        return super.simpleBrokerMessageHandler(inboundChannel, outboundChannel,
                brokerChannel, userDestinationResolver);
    }

    @Override
    @Bean("mailSpyStompBrokerRelayMessageHandler")
    public AbstractBrokerMessageHandler stompBrokerRelayMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyUserDestinationMessageHandler") UserDestinationMessageHandler userDestinationMessageHandler,
            @Qualifier("mailSpyUserRegistryMessageHandler") @Nullable MessageHandler userRegistryMessageHandler,
            @Qualifier("mailSpyUserDestinationResolver") UserDestinationResolver userDestinationResolver) {
        return super.stompBrokerRelayMessageHandler(
                inboundChannel, outboundChannel, brokerChannel,
                userDestinationMessageHandler, userRegistryMessageHandler, userDestinationResolver);
    }


    @Override
    @Bean("mailSpyUserDestinationMessageHandler")
    public UserDestinationMessageHandler userDestinationMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel clientInboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel clientOutboundChannel,
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyUserDestinationResolver") UserDestinationResolver userDestinationResolver) {
        return super.userDestinationMessageHandler(clientInboundChannel, clientOutboundChannel,
                brokerChannel, userDestinationResolver);
    }

    @Override
    @Bean("mailSpyUserRegistryMessageHandler")
    public MessageHandler userRegistryMessageHandler(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyUserRegistry") SimpUserRegistry userRegistry,
            @Qualifier("mailSpyBrokerMessagingTemplate") SimpMessagingTemplate brokerMessagingTemplate,
            @Qualifier("mailSpyMessageBrokerTaskScheduler") TaskScheduler scheduler) {
        return super.userRegistryMessageHandler(inboundChannel, outboundChannel, userRegistry, brokerMessagingTemplate, scheduler);
    }

    @Override
    @Bean("mailSpyMessageBrokerTaskScheduler")
    public TaskScheduler messageBrokerTaskScheduler() {
        return super.messageBrokerTaskScheduler();
    }

    @Override
    @Bean("mailSpyBrokerMessagingTemplate")
    public SimpMessagingTemplate brokerMessagingTemplate(
            @Qualifier("mailSpyBrokerChannel") AbstractSubscribableChannel brokerChannel,
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel,
            @Qualifier("mailSpyBrokerMessageConverter") CompositeMessageConverter brokerMessageConverter) {
        return super.brokerMessagingTemplate(brokerChannel, inboundChannel, outboundChannel, brokerMessageConverter);
    }

    @Bean
    @Primary
    public SimpMessagingTemplate primaryBrokerMessagingTemplate(
            @Qualifier("brokerMessagingTemplate") @Autowired(required = false) SimpMessagingTemplate brokerMessagingTemplate) {
        return brokerMessagingTemplate;
    }

    @Override
    @Bean("mailSpyBrokerMessageConverter")
    public CompositeMessageConverter brokerMessageConverter() {
        return super.brokerMessageConverter();
    }

    @Override
    @Bean("mailSpyUserDestinationResolver")
    public UserDestinationResolver userDestinationResolver(
            @Qualifier("mailSpyUserRegistry") SimpUserRegistry userRegistry,
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel) {
        return super.userDestinationResolver(userRegistry, inboundChannel, outboundChannel);
    }

    @Override
    @Bean("mailSpyUserRegistry")
    public SimpUserRegistry userRegistry(
            @Qualifier("mailSpyClientInboundChannel") AbstractSubscribableChannel inboundChannel,
            @Qualifier("mailSpyClientOutboundChannel") AbstractSubscribableChannel outboundChannel) {
        return super.userRegistry(inboundChannel, outboundChannel);
    }

}
