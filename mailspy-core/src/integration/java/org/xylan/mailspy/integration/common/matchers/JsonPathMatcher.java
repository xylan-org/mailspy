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

package org.xylan.mailspy.integration.common.matchers;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A Hamcrest matcher that allows extracting values using JSONPath and matching them against any matcher.
 */
public class JsonPathMatcher extends BaseMatcher<String> {

    private final String jsonPath;
    private final Matcher<?> valueMatcher;

    public JsonPathMatcher(String jsonPath, Matcher<?> valueMatcher) {
        this.jsonPath = jsonPath;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object json) {
        boolean result = false;
        if (json instanceof String) {
            String value = JsonPath.parse((String) json).read(jsonPath, String.class);
            result = valueMatcher.matches(value);
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("result of expression '" + jsonPath + "' to be ");
        valueMatcher.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText(item.toString());
    }

}
