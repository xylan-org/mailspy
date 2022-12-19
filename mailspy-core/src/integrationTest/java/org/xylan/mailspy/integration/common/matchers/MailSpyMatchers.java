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

import org.hamcrest.Matcher;
import org.springframework.messaging.Message;

/**
 * Static factory for MailSpy's custom Hamcrest matchers.
 */
public final class MailSpyMatchers {

    private MailSpyMatchers() {}

    /**
     * Creates a matcher to match email message headers in a base64 encoded email message.
     * @param headerName The header name to match.
     * @param valueMatcher The matcher to apply on the header's value.
     * @return The constructed matcher.
     */
    public static Matcher<String> emailHeaderMatches(String headerName, Matcher<String> valueMatcher) {
        return new EmailHeaderMatcher(headerName, valueMatcher);
    }

    /**
     * Creates a matcher to match the body of a base64 encoded email message.
     * @param valueMatcher The matcher to apply on the message's body.
     * @return The constructed matcher.
     */
    public static Matcher<String> emailTextMatches(Matcher<String> valueMatcher) {
        return new EmailTextMatcher(valueMatcher);
    }

    /**
     * Creates a matcher that allows extracting values using JSONPath and matching them against any matcher.
     * @param jsonPath The JSONPath expression of the value to extract.
     * @param valueMatcher The matcher to match the extracted value against.
     * @return The constructed matcher.
     */
    public static Matcher<String> jsonPathMatches(String jsonPath, Matcher<?> valueMatcher) {
        return new JsonPathMatcher(jsonPath, valueMatcher);
    }

    public static Matcher<Message<?>> messagePayloadMatches(Matcher<?> valueMatcher) {
        return new MessagePayloadMatcher(valueMatcher);
    }

    public static Matcher<Message<?>> messageHeaderMatches(String headerName, Matcher<?> valueMatcher) {
        return new MessageHeaderMatcher(headerName, valueMatcher);
    }
}
