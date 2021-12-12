package org.xylan.mailspy.core.integration.common.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailTextMatcher extends BaseMatcher<String> {

    private static final Pattern MAIL_CONTENT_PATTERN = Pattern.compile("^(?:[A-Za-z0-9-]+\\s*:(?:\\s+.*[\\r\\n]+)*)+(.*)$");

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
            } catch (IllegalArgumentException exception) {
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
        description.appendText("the email text to be ")
            .appendDescriptionOf(valueMatcher);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("the email text was missing or did not match");
    }

}
