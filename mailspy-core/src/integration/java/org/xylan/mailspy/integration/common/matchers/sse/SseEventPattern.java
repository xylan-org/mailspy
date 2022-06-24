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
