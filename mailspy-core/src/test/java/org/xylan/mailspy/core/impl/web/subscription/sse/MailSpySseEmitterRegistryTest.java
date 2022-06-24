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

package org.xylan.mailspy.core.impl.web.subscription.sse;

import java.io.IOException;
import java.util.Iterator;

import lombok.SneakyThrows;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willThrow;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailSpySseEmitterRegistryTest {

    @Mock
    private SseEmitter sseEmitter;

    @Captor
    private ArgumentCaptor<SseEventBuilder> sseEventCaptor;

    @InjectMocks
    private MailSpySseEmitterRegistry underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setSseEmitterSupplier(() -> sseEmitter);
    }

    @Test
    @SneakyThrows
    public void createEmitterShouldSendConnectedEvent() {
        // GIVEN
        // WHEN
        underTest.createEmitter();

        // THEN
        then(sseEmitter).should().send(sseEventCaptor.capture());
        Iterator<DataWithMediaType> sseEventIterator = getSseEventIterator();
        assertEquals(sseEventIterator.next().getData(), "event:connected\ndata:");
        assertEquals(sseEventIterator.next().getData(), "connected");
    }

    @Test
    @SneakyThrows
    public void createEmitterShouldCompleteEmitterWithErrorWhenSendThrowsException() {
        // GIVEN
        IOException exception = new IOException("test I/O exception");
        willThrow(exception).given(sseEmitter).send(any());

        // WHEN
        underTest.createEmitter();

        // THEN
        then(sseEmitter).should().completeWithError(exception);
    }

    @Test
    @SneakyThrows
    public void broadcastShouldSendGivenDataThroughAliveEmitters() {
        // GIVEN
        underTest.createEmitter();

        // WHEN
        underTest.broadcast("testEvent", "testData");

        // THEN
        then(sseEmitter).should(times(2)).send(sseEventCaptor.capture());
        Iterator<DataWithMediaType> sseEventIterator = getSseEventIterator();
        assertEquals(sseEventIterator.next().getData(), "event:testEvent\ndata:");
        assertEquals(sseEventIterator.next().getData(), "testData");
    }

    @Test
    @SneakyThrows
    public void broadcastShouldSendOnlyEventTypeWhenDataIsNull() {
        // GIVEN
        underTest.createEmitter();

        // WHEN
        underTest.broadcast("testEvent", null);

        // THEN
        then(sseEmitter).should(times(2)).send(sseEventCaptor.capture());
        Iterator<DataWithMediaType> sseEventIterator = getSseEventIterator();
        assertEquals(sseEventIterator.next().getData(), "event:testEvent\n\n");
    }

    @Test
    @SneakyThrows
    public void broadcastShouldCompleteEmitterWithErrorWhenSendThrowsException() {
        // GIVEN
        underTest.createEmitter();
        IOException exception = new IOException("test I/O exception");
        willThrow(exception).given(sseEmitter).send(any());

        // WHEN
        underTest.broadcast("testEvent", null);

        // THEN
        then(sseEmitter).should().completeWithError(exception);
    }

    private Iterator<DataWithMediaType> getSseEventIterator() {
        return sseEventCaptor.getValue().build().iterator();
    }
}
