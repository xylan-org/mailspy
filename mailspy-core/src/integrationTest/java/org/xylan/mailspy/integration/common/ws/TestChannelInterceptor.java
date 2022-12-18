package org.xylan.mailspy.integration.common.ws;

import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ChannelInterceptor} that caches messages.
 */
public class TestChannelInterceptor implements ChannelInterceptor {

    private final BlockingQueue<Message<?>> messages = new ArrayBlockingQueue<>(100);

    @SneakyThrows
    public Message<?> awaitMessage(long timeoutInSeconds) {
        return this.messages.poll(timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        this.messages.add(message);
        return message;
    }

}