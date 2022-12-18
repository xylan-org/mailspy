package org.xylan.mailspy.integration.common.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.messaging.Message;

public class MessageHeaderMatcher extends BaseMatcher<Message<?>> {

    private final String headerName;
    private final Matcher<?> valueMatcher;

    public MessageHeaderMatcher(String headerName, Matcher<?> valueMatcher) {
        this.headerName = headerName;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object actual) {
        boolean result = false;
        if (actual instanceof Message) {
            Message<?> message = (Message<?>) actual;
            result = valueMatcher.matches(message.getHeaders().get(headerName));
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("message header '" + headerName + "' to be ");
        valueMatcher.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof Message) {
            Message<?> message = (Message<?>) item;
            description.appendText("was ")
                .appendValue(message.getHeaders().get(headerName));
        } else {
            description.appendText("given object was not a message");
        }
    }

}
