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

package org.xylan.mailspy.integration.common.ws;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

public class WebSocketTestStub {

    private final TestChannelInterceptor brokerChannelInterceptor;
    private final AbstractMessageChannel inboundChannel;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTestStub(
            ApplicationContext context,
            String inboundChannelName,
            String brokerChannelName,
            String messagingTemplateName) {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.inboundChannel = context.getBean(inboundChannelName, AbstractMessageChannel.class);
        this.messagingTemplate = context.getBean(messagingTemplateName, SimpMessagingTemplate.class);
        AbstractSubscribableChannel mailSpyBrokerChannel =
                context.getBean(brokerChannelName, AbstractSubscribableChannel.class);
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
