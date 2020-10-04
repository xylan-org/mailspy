package org.abelk.devmailserver.core.web.support.handlermapping;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.springframework.web.HttpRequestHandler;
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

    private static Map<String, ?> convertUrlMap(final Map<String, ?> urlMap) {
        return urlMap.entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(), e -> processHandler(e.getValue())));
    }

    private static Object processHandler(final Object handler) {
        Object result;
        if (handler instanceof HttpRequestHandler) {
            result = handler;
        } else {
            result = createHandlerMethod(handler);
        }
        return result;
    }

    private static HandlerMethod createHandlerMethod(final Object bean) {
        return new HandlerMethod(bean, getFirstAnnotatedMethod(bean));
    }

    private static Method getFirstAnnotatedMethod(final Object bean) {
        return Arrays.stream(bean.getClass().getMethods())
                .filter(b -> b.getAnnotation(org.abelk.devmailserver.core.web.support.handlermapping.HandlerMethod.class) != null)
                .findFirst()
                .orElseThrow();
    }

}
