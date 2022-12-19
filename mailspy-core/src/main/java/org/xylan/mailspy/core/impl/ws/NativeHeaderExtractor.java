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

package org.xylan.mailspy.core.impl.ws;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * Helper component to extract native headers from WebSocket messages.
 */
@Component
public class NativeHeaderExtractor {

    /**
     * Extracts the given native header from the message.
     * @param message The message to extract from.
     * @param headerName The name of the header to extract.
     * @return The extracted header value.
     */
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
        return (Map<String, List<String>>) message.getHeaders().get(NativeMessageHeaderAccessor.NATIVE_HEADERS);
    }
}
