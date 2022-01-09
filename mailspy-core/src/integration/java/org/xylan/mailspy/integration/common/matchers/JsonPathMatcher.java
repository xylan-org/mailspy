package org.xylan.mailspy.integration.common.matchers;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class JsonPathMatcher extends BaseMatcher<String>  {

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
