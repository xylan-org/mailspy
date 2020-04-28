package org.abelk.devmailserver.core.web.handlermapping;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

public class SimpleUrlHandlerMethodMapping extends SimpleUrlHandlerMapping {

    public SimpleUrlHandlerMethodMapping() {
        super();
    }

    public SimpleUrlHandlerMethodMapping(final Map<String, ?> urlMap, final int order) {
        super(urlMap, order);
    }

    public SimpleUrlHandlerMethodMapping(final Map<String, ?> urlMap) {
        super(urlMap);
    }

    @Override
    public void setUrlMap(final Map<String, ?> urlMap) {
        super.setUrlMap(convertUrlMap(urlMap));
    }

    private static Map<String, HandlerMethod> convertUrlMap(final Map<String, ?> urlMap) {
        return urlMap.entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(), e -> createHandlerMethod(e.getValue())));
    }

    private static HandlerMethod createHandlerMethod(final Object bean) {
        return new HandlerMethod(bean, getFirstAnnotatedMethod(bean));
    }

    private static Method getFirstAnnotatedMethod(final Object bean) {
        return Arrays.stream(bean.getClass().getMethods())
                .filter(b -> b.getAnnotation(org.abelk.devmailserver.core.web.handlermapping.HandlerMethod.class) != null)
                .findFirst()
                .orElseThrow();
    }

}
