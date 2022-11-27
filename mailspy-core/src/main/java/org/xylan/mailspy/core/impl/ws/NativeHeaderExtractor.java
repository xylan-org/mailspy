package org.xylan.mailspy.core.impl.ws;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class NativeHeaderExtractor {

    public String getHeader(Message<?> message, String headerName) {
        Map<String, List<String>> nativeHeaders = getNativeHeaders(message);
        return Optional.ofNullable(nativeHeaders)
                .map(map -> map.get(headerName))
                .filter(list -> !list.isEmpty())
                .map(item -> item.get(0))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getNativeHeaders(Message<?> message) {
        return (Map<String, List<String>>) message.getHeaders()
                .get(NativeMessageHeaderAccessor.NATIVE_HEADERS);
    }

}
