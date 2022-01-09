package org.xylan.mailspy.integration.common.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;
import org.xylan.mailspy.integration.common.matchers.sse.SseEventsPattern;
import org.xylan.mailspy.integration.common.matchers.sse.SseEventsPatternMatcher;

public class MailSpyMatchers {

    private MailSpyMatchers() {}

    public static Matcher<String> emailHeaderMatches(String headerName, Matcher<String> valueMatcher) {
        return new EmailHeaderMatcher(headerName, valueMatcher);
    }

    public static Matcher<String> emailTextMatches(Matcher<String> valueMatcher) {
        return new EmailTextMatcher(valueMatcher);
    }

    public static Matcher<String> isValidUuid() {
        return MatchesPattern.matchesPattern("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");
    }

    public static Matcher<String> sseEventsMatchPattern(SseEventsPattern sseEventsPattern) {
        return new SseEventsPatternMatcher(sseEventsPattern);
    }

    public static Matcher<String> jsonPathMatches(String jsonPath, Matcher<?> valueMatcher) {
        return new JsonPathMatcher(jsonPath, valueMatcher);
    }

}
