package org.xylan.mailspy.core.config.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.xylan.mailspy.core.config.MailSpyProperties;

@Configuration
@EnableWebSocketMessageBroker
public class MailSpyWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private MailSpyProperties properties;

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
        registry.setMessageSizeLimit(properties.getWebSocket().getMaxMessageBytes());
        registry.setSendBufferSizeLimit(properties.getWebSocket().getMaxSendBufferBytes());
    }
}
