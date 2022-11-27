package org.xylan.mailspy.core.impl.ws;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.testng.Assert.*;

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
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            NATIVE_HEADERS, Collections.emptyMap()
        ));

        // WHEN
        String result = underTest.getHeader(message, "header");

        // THEN
        assertNull(result);
    }

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNativeHeaderWithGivenNameButIsEmptyList() {
        // GIVEN
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            NATIVE_HEADERS, Map.of("header", Collections.emptyList())
        ));

        // WHEN
        String result = underTest.getHeader(message, "header");

        // THEN
        assertNull(result);
    }

    @Test
    public void getHeaderShouldReturnNullWhenMessageHasNativeHeaderWithGivenNameAndHasAtLeastOneElement() {
        // GIVEN
        String expected = "1234";
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            NATIVE_HEADERS, Map.of("header", List.of(expected))
        ));

        // WHEN
        String actual = underTest.getHeader(message, "header");

        // THEN
        assertEquals(actual, expected);
    }

}