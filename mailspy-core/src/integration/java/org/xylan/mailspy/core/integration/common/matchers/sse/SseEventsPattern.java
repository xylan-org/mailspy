package org.xylan.mailspy.core.integration.common.matchers.sse;

import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;

public class SseEventsPattern {

    private final List<SseEventPattern> sseEventPatterns = new LinkedList<>();

    public static SseEventsPattern create() {
        return new SseEventsPattern();
    }

    public SseEventsPattern name(Matcher<String> matcher) {
        sseEventPatterns.add(new SseEventPattern("event", matcher));
        return this;
    }

    public SseEventsPattern data(Matcher<String> matcher) {
        sseEventPatterns.add(new SseEventPattern("data", matcher));
        return this;
    }

    public SseEventsPattern emptyLine() {
        sseEventPatterns.add(SseEventPattern.EMPTY_LINE);
        return this;
    }

    public List<SseEventPattern> getSseEventPatterns() {
        return List.copyOf(sseEventPatterns);
    }

}
