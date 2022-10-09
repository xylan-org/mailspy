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

package org.xylan.mailspy.app.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Extracts plain text from HTML markup.
 */
@Component
public class HtmlTextExtractor {

    private static final Pattern TEXT_TAGS_PATTERN =
            Pattern.compile("<(?:p|h\\d).*?>(.*?)</(?:p|h\\d)>", Pattern.MULTILINE);
    private static final String HTML_TAG_PATTERN = "<.*?>";

    /**
     * Extracts plain text from HTML markup.
     * @param htmlText The HTML text to process.
     * @return The extracted plain text.
     */
    public String extractText(String htmlText) {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = TEXT_TAGS_PATTERN.matcher(htmlText);
        while (matcher.find()) {
            stringBuilder.append(matcher.group(1).replaceAll(HTML_TAG_PATTERN, ""));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
