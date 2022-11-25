package org.xylan.mailspy.core.impl.ws;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;

public class DisabledSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

    /**
     * Create a new instance with the given
     * message channels and broker messaging template.
     *
     * @param clientInboundChannel  the channel for receiving messages from clients (e.g. WebSocket clients)
     * @param clientOutboundChannel the channel for messages to clients (e.g. WebSocket clients)
     * @param brokerTemplate        a messaging template to send application messages to the broker
     */
    public DisabledSimpAnnotationMethodMessageHandler(SubscribableChannel clientInboundChannel, MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {
        super(clientInboundChannel, clientOutboundChannel, brokerTemplate);
    }

    @Override
    public void afterPropertiesSet() {
        // left blank intentionally
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

}
