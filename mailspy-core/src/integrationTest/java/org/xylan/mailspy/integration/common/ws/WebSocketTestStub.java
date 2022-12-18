package org.xylan.mailspy.integration.common.ws;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import static org.testng.Assert.assertNotNull;

public class WebSocketTestStub {

    private final TestChannelInterceptor brokerChannelInterceptor;

    public WebSocketTestStub(ApplicationContext context) {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        AbstractSubscribableChannel mailSpyBrokerChannel = (AbstractSubscribableChannel) context.getBean("mailSpyBrokerChannel");
        mailSpyBrokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    public Message<?> awaitMessage() {
        Message<?> message = brokerChannelInterceptor.awaitMessage(5);
        assertNotNull(message);
        return message;
    }

}
