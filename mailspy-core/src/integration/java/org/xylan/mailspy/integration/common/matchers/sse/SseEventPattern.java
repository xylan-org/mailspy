package org.xylan.mailspy.integration.common.matchers.sse;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hamcrest.Matcher;

/**
 * A pattern to describe how a single event from a series of SSE events should look like.
 */
@Data
@AllArgsConstructor
public class SseEventPattern {

    public static final SseEventPattern EMPTY_LINE = new SseEventPattern("__EMPTY_LINE__", null);

    private String key;
    private Matcher<String> valueMatcher;

    /**
     * Checks if this pattern is an empty line.
     * @return {@code true} if pattern represents an empty line.
     */
    public boolean isEmptyLine() {
        return this == EMPTY_LINE;
    }

}
