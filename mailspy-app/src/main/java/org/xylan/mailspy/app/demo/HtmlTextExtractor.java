package org.xylan.mailspy.app.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class HtmlTextExtractor {

    private static final Pattern TEXT_TAGS_PATTERN = Pattern.compile("<(?:p|h\\d).*?>(.*?)</(?:p|h\\d)>", Pattern.MULTILINE);
    private static final String HTML_TAG_PATTERN = "<.*?>";

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
