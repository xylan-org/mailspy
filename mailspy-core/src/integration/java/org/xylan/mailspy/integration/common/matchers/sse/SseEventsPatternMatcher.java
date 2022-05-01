package org.xylan.mailspy.integration.common.matchers.sse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * A Hamcrest matcher to match a series of SSE events against a pattern.
 */
public class SseEventsPatternMatcher extends BaseMatcher<String> {

    private static final Pattern SSE_EVENT_LINE_PATTERN = Pattern.compile("^(.*?):(.*)$");

    private final SseEventsPattern sseEventsPattern;

    public SseEventsPatternMatcher(SseEventsPattern sseEventsPattern) {
        this.sseEventsPattern = sseEventsPattern;
    }

    @Override
    public boolean matches(Object sseEventsAsString) {
        boolean result = false;
        if (sseEventsAsString instanceof String) {
            result = stringMatches((String) sseEventsAsString);
        }
        return result;
    }

    private boolean stringMatches(String sseEventsAsString) {
        boolean result = false;
        String[] lines = sseEventsAsString.split("\n", -1);
        List<SseEventPattern> sseEventPatterns = sseEventsPattern.getSseEventPatterns();
        if (lines.length == sseEventPatterns.size()) {
            result = linesMatchPatterns(lines, sseEventPatterns);
        }
        return result;
    }

    private boolean linesMatchPatterns(String[] lines, List<SseEventPattern> sseEventPatterns) {
        boolean result = true;
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            SseEventPattern sseEventPattern = sseEventPatterns.get(i);
            if (sseEventPattern.isEmptyLine()) {
                if (!line.isEmpty()) {
                    result = false;
                    break;
                }
            } else {
                result = lineMatchesPattern(line, sseEventPattern);
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    private boolean lineMatchesPattern(String line, SseEventPattern sseEventPattern) {
        boolean result = true;
        Matcher matcher = SSE_EVENT_LINE_PATTERN.matcher(line);
        if (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            if (key == null || value == null
                || !key.equals(sseEventPattern.getKey())
                || !sseEventPattern.getValueMatcher().matches(value)
            ) {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("\n");
        sseEventsPattern.getSseEventPatterns()
            .forEach(sseEventPattern -> {
                if (sseEventPattern.isEmptyLine()) {
                    description.appendText("<empty line>");
                } else {
                    description.appendText(sseEventPattern.getKey())
                        .appendText(":(...) <-- this to be ");
                    sseEventPattern.getValueMatcher().describeTo(description);
                }
                description.appendText("\n");
            });
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("found:");
        if (item instanceof String) {
            description.appendText("\n")
                .appendText(item.toString());
        } else {
            description.appendText("item was not a String.");
        }
    }

}
