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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * A Hamcrest matcher to match email message headers in a base64 encoded email message.
 */
public class EmailHeaderMatcher extends BaseMatcher<String> {

    private static final Pattern MAIL_HEADERS_PATTERN =
            Pattern.compile("([A-Za-z0-9-]+)\\s*:\\s*((?:.*[\\r\\n]+)(?:\\s+.*[\\r\\n]+)*)");

    private final String header;
    private final org.hamcrest.Matcher<String> valueMatcher;

    public EmailHeaderMatcher(String header, org.hamcrest.Matcher<String> valueMatcher) {
        this.header = header;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object mailMessageBase64) {
        boolean result = false;
        if (mailMessageBase64 instanceof String) {
            try {
                String mailMessage = decodeBase64((String) mailMessageBase64);
                Map<String, String> headers = getHeaders(mailMessage);
                result = valueMatcher.matches(headers.get(header.toLowerCase(Locale.US)));
            } catch (IllegalArgumentException ignored) {
                // ignored; result remains false
            }
        }
        return result;
    }

    private Map<String, String> getHeaders(String mailMessage) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = MAIL_HEADERS_PATTERN.matcher(mailMessage);
        while (matcher.find()) {
            String header = matcher.group(1);
            String value = matcher.group(2);
            if (header != null && value != null) {
                result.put(header.toLowerCase(Locale.US), value.trim());
            }
        }
        return result;
    }

    private String decodeBase64(String mailMessageBase64) {
        return new String(Base64.getDecoder().decode(mailMessageBase64));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the '" + header + "' email header to be ").appendDescriptionOf(valueMatcher);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("the '" + header + "' email header could not be found, or did not match");
    }
}
