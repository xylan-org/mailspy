package org.xylan.mailspy.core.integration.common.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailHeaderMatcher extends BaseMatcher<String> {

    private static final Pattern MAIL_HEADERS_PATTERN = Pattern.compile("([A-Za-z0-9-]+)\\s*:\\s*((?:.*[\\r\\n]+)(?:\\s+.*[\\r\\n]+)*)");

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
                result = valueMatcher.matches(headers.get(header.toLowerCase()));
            } catch (IllegalArgumentException exception) {
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
                result.put(header.toLowerCase(), value.trim());
            }
        }
        return result;
    }

    private String decodeBase64(String mailMessageBase64) {
        return new String(Base64.getDecoder().decode(mailMessageBase64));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the '" + header + "' email header to be ")
            .appendDescriptionOf(valueMatcher);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("the '" + header + "' email header could not be found, or did not match");
    }

}
