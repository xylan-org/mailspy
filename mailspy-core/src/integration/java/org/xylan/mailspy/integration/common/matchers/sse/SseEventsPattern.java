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
