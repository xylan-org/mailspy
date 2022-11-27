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

import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.testng.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.mockito.InjectMocks;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class NativeHeaderExtractorTest {

    @InjectMocks
    private NativeHeaderExtractor underTest;

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNoNativeHeadersHeader() {
        // GIVEN
        Message<?> message = new GenericMessage<>(new Object(), Collections.emptyMap());

        // WHEN
        String result = underTest.getHeader(message, "header");

        // THEN
        assertNull(result);
    }

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNoNativeHeaderWithGivenName() {
        // GIVEN
        Message<?> message = new GenericMessage<>(new Object(), Map.of(NATIVE_HEADERS, Collections.emptyMap()));

        // WHEN
        String result = underTest.getHeader(message, "header");

        // THEN
        assertNull(result);
    }

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNativeHeaderWithGivenNameButIsEmptyList() {
        // GIVEN
        Message<?> message =
                new GenericMessage<>(new Object(), Map.of(NATIVE_HEADERS, Map.of("header", Collections.emptyList())));

        // WHEN
        String result = underTest.getHeader(message, "header");

        // THEN
        assertNull(result);
    }

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNativeHeaderWithGivenNameAndHasAtLeastOneElement() {
        // GIVEN
        String expected = "1234";
        Message<?> message =
                new GenericMessage<>(new Object(), Map.of(NATIVE_HEADERS, Map.of("header", List.of(expected))));

        // WHEN
        String actual = underTest.getHeader(message, "header");

        // THEN
        assertEquals(actual, expected);
    }
}
