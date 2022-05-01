package org.xylan.mailspy.integration.common.matchers.sse;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Matcher;

/**
 * A pattern to describe how a series of SSE events should look like. See existing usages for examples.
 */
public class SseEventsPattern {

    private final List<SseEventPattern> sseEventPatterns = new LinkedList<>();

    /**
     * Creates an events pattern.
     * @return The created pattern.
     */
    public static SseEventsPattern create() {
        return new SseEventsPattern();
    }

    /**
     * Adds a pattern to match the event's name.
     * @param matcher The matcher to match the name against.
     * @return The same {@link SseEventsPattern} instance.
     */
    public SseEventsPattern name(Matcher<String> matcher) {
        sseEventPatterns.add(new SseEventPattern("event", matcher));
        return this;
    }

    /**
     * Adds a pattern to match the event's data.
     * @param matcher The matcher to match the data against.
     * @return The same {@link SseEventsPattern} instance.
     */
    public SseEventsPattern data(Matcher<String> matcher) {
        sseEventPatterns.add(new SseEventPattern("data", matcher));
        return this;
    }

    /**
     * Adds a pattern to match an empty line.
     * @return The same {@link SseEventsPattern} instance.
     */
    public SseEventsPattern emptyLine() {
        sseEventPatterns.add(SseEventPattern.EMPTY_LINE);
        return this;
    }

    /**
     * Gets the list of event patterns.
     * @return The event patterns.
     */
    public List<SseEventPattern> getSseEventPatterns() {
        return List.copyOf(sseEventPatterns);
    }

}
