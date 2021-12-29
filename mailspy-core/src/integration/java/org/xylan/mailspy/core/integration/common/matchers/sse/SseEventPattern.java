package org.xylan.mailspy.core.integration.common.matchers.sse;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hamcrest.Matcher;

@Data
@AllArgsConstructor
public class SseEventPattern {

    public static final SseEventPattern EMPTY_LINE = new SseEventPattern("__EMPTY_LINE__", null);

    private String key;
    private Matcher<String> valueMatcher;

    public boolean isEmptyLine() {
        return this == EMPTY_LINE;
    }

}
