package org.xylan.mailspy.integration.common.ws;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class WebSocketTestStub {

    private final TestChannelInterceptor brokerChannelInterceptor;
    private final AbstractMessageChannel inboundChannel;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTestStub(ApplicationContext context, String inboundChannelName, String brokerChannelName, String messagingTemplateName) {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.inboundChannel = context.getBean(inboundChannelName, AbstractMessageChannel.class);
        this.messagingTemplate = context.getBean(messagingTemplateName, SimpMessagingTemplate.class);
        AbstractSubscribableChannel mailSpyBrokerChannel = context.getBean(brokerChannelName, AbstractSubscribableChannel.class);
        mailSpyBrokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    public SimpMessagingTemplate getMessagingTemplate() {
        return messagingTemplate;
    }

    public Message<?> awaitMessageSent() {
        Message<?> message = brokerChannelInterceptor.awaitMessage(2);
        assertNotNull(message);
        return message;
    }

    public void awaitNoMessageSent() {
        Message<?> message = brokerChannelInterceptor.awaitMessage(2);
        assertNull(message);
    }

    public void simulateMessageReceived(String payload, Map<String, Object> headers) {
        simulateMessageReceived(payload.getBytes(StandardCharsets.UTF_8), new MessageHeaders(headers));
    }

    public void simulateMessageReceived(Map<String, Object> headers) {
        simulateMessageReceived(new byte[0], headers);
    }

    protected void simulateMessageReceived(byte[] payload, Map<String, Object> headers) {
        Message<?> message = MessageBuilder.createMessage(payload, new MessageHeaders(headers));
        inboundChannel.send(message);
    }

}
