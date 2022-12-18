package org.xylan.mailspy.integration.common.ws;

import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A ChannelInterceptor that caches messages.
 */
public class TestChannelInterceptor implements ChannelInterceptor {

    private final BlockingQueue<Message<?>> messages = new ArrayBlockingQueue<>(100);

    private final List<String> destinationPatterns = new ArrayList<>();

    private final PathMatcher matcher = new AntPathMatcher();

    public void setIncludedDestinations(String... patterns) {
        this.destinationPatterns.addAll(Arrays.asList(patterns));
    }

    @SneakyThrows
    public Message<?> awaitMessage(long timeoutInSeconds) {
        return this.messages.poll(timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        if (this.destinationPatterns.isEmpty()) {
            this.messages.add(message);
        }
        else {
            StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
            if (headers.getDestination() != null) {
                for (String pattern : this.destinationPatterns) {
                    if (this.matcher.match(pattern, headers.getDestination())) {
                        this.messages.add(message);
                        break;
                    }
                }
            }
        }
        return message;
    }

}