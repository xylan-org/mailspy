package org.xylan.mailspy.integration.common.matchers;

import java.nio.charset.StandardCharsets;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.messaging.Message;

public class MessagePayloadMatcher extends BaseMatcher<Message<?>> {

    private final Matcher<?> valueMatcher;

    public MessagePayloadMatcher(Matcher<?> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object actual) {
        boolean result = false;
        if (actual instanceof Message) {
            String payload = extractPayload((Message<?>) actual);
            result = valueMatcher.matches(payload);
        }
        return result;
    }

    private String extractPayload(Message<?> actual) {
        return new String((byte[]) actual.getPayload(), StandardCharsets.UTF_8);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("message payload to be ");
        valueMatcher.describeTo(description);
    }

}
