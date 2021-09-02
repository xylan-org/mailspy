package org.abelk.devmailserver.core.web.subscription.controller;

import org.abelk.devmailserver.core.web.subscription.sse.SseEmitterRegistry;
import org.mockito.*;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsSubscriptionControllerTest {

    @Mock
    private SseEmitterRegistry sseEmitterRegistry;

    @InjectMocks
    private MailsSubscriptionController underTest;

    @Test
    public void createSseEmitterShouldReturnResultOfSseEmitterRegistry() {
        // GIVEN
        SseEmitter expected = mock(SseEmitter.class);
        given(sseEmitterRegistry.createEmitter()).willReturn(expected);

        // WHEN
        SseEmitter actual = underTest.createSseEmitter();

        // THEN
        assertEquals(actual, expected);
    }

}
