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

package org.xylan.mailspy.integration.common.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.messaging.Message;

/**
 * A Hamcrest matcher that allows matching a given header of a {@link Message}.
 */
public class MessageHeaderMatcher extends BaseMatcher<Message<?>> {

    private final String headerName;
    private final Matcher<?> valueMatcher;

    /**
     * Creates a new instance.
     * @param headerName The name of the header to match.
     * @param valueMatcher The matcher to apply on the header value.
     */
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
            description.appendText("was ").appendValue(message.getHeaders().get(headerName));
        } else {
            description.appendText("given object was not a message");
        }
    }
}
