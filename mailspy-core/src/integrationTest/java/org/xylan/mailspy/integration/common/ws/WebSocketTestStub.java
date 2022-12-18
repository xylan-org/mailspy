package org.xylan.mailspy.integration.common.ws;

import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.testng.Assert.assertNotNull;

public class WebSocketTestStub {

    private final TestChannelInterceptor brokerChannelInterceptor;

    public WebSocketTestStub(ApplicationContext context) {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        AbstractSubscribableChannel mailSpyBrokerChannel = (AbstractSubscribableChannel) context.getBean("mailSpyBrokerChannel");
        mailSpyBrokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    public String awaitStringReply() {
        Message<?> message = brokerChannelInterceptor.awaitMessage(5);
        assertNotNull(message);
        Object payload = message.getPayload();
        assertThat(payload, instanceOf(byte[].class));
        return new String((byte[]) payload, StandardCharsets.UTF_8);
    }
    
}
