package org.xylan.mailspy.core.config.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.xylan.mailspy.core.config.MailSpyProperties;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class MailSpyWebSocketConfig extends WebSocketMessageBrokerConfigurationSupport {

    @Autowired
    private MailSpyProperties properties;

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/ws/dest")
            .setUserDestinationPrefix("/ws/topic/user")
            .setPreservePublishOrder(true)
            .enableSimpleBroker("/ws/topic");
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

}
