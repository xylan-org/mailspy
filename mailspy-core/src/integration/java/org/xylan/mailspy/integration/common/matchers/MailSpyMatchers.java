package org.xylan.mailspy.integration.common.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;
import org.xylan.mailspy.integration.common.matchers.sse.SseEventsPattern;
import org.xylan.mailspy.integration.common.matchers.sse.SseEventsPatternMatcher;

/**
 * Static factory for MailSpy's custom Hamcrest matchers.
 */
public class MailSpyMatchers {

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
     * Creates a matcher to match UUID identifier strings.
     * @return The constructed matcher.
     */
    public static Matcher<String> isValidUuid() {
        return MatchesPattern.matchesPattern("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");
    }

    /**
     * Creates a matcher to match a list of emitted SSE events.
     * @param sseEventsPattern The pattern that the list of events will be matched against.
     * @return The constructed matcher.
     */
    public static Matcher<String> sseEventsMatchPattern(SseEventsPattern sseEventsPattern) {
        return new SseEventsPatternMatcher(sseEventsPattern);
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

}
