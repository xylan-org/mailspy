package org.xylan.mailspy.core.impl.web.subscription.sse;

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

import java.io.IOException;
import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class SseEmitterRegistryTest {

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