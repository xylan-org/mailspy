package org.xylan.mailspy.integration.common.ws;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

import static org.testng.Assert.assertNotNull;

public class WebSocketTestStub {

    private final TestChannelInterceptor brokerChannelInterceptor;
    private final AbstractMessageChannel inboundChannel;

    public WebSocketTestStub(ApplicationContext context) {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.inboundChannel = context.getBean("mailSpyClientInboundChannel", AbstractMessageChannel.class);
        AbstractSubscribableChannel mailSpyBrokerChannel = context.getBean("mailSpyBrokerChannel", AbstractSubscribableChannel.class);
        mailSpyBrokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    public Message<?> awaitMessage() {
        Message<?> message = brokerChannelInterceptor.awaitMessage(5);
        assertNotNull(message);
        return message;
    }

    public void sendMessage(String payload, Map<String, Object> headers) {
        sendMessage(payload.getBytes(StandardCharsets.UTF_8), new MessageHeaders(headers));
    }

    public void sendMessage(Map<String, Object> headers) {
        sendMessage(new byte[0], headers);
    }

    protected void sendMessage(byte[] payload, Map<String, Object> headers) {
        Message<?> message = MessageBuilder.createMessage(payload, new MessageHeaders(headers));
        inboundChannel.send(message);
    }

}
