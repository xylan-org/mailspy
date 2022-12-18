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

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * A Hamcrest matcher to match the body of a base64 encoded email message.
 */
public class EmailTextMatcher extends BaseMatcher<String> {

    private static final Pattern MAIL_CONTENT_PATTERN =
            Pattern.compile("^(?:[A-Za-z0-9-]+\\s*:(?:\\s+.*[\\r\\n]+)*)+(.*)$");

    private final org.hamcrest.Matcher<String> valueMatcher;

    public EmailTextMatcher(org.hamcrest.Matcher<String> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object mailMessageBase64) {
        boolean result = false;
        if (mailMessageBase64 instanceof String) {
            try {
                String mailMessage = decodeBase64((String) mailMessageBase64);
                String text = getText(mailMessage);
                result = valueMatcher.matches(text);
            } catch (IllegalArgumentException ignored) {
                // ignored; result remains false
            }
        }
        return result;
    }

    private String getText(String mailMessage) {
        String result = null;
        Matcher matcher = MAIL_CONTENT_PATTERN.matcher(mailMessage);
        if (matcher.find()) {
            String text = matcher.group(1);
            if (text != null) {
                result = text.trim();
            }
        }
        return result;
    }

    private String decodeBase64(String mailMessageBase64) {
        return new String(Base64.getDecoder().decode(mailMessageBase64));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the email text to be ").appendDescriptionOf(valueMatcher);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("the email text was missing or did not match");
    }
}
