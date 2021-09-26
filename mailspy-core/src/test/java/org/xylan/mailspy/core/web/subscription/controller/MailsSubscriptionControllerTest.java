package org.xylan.mailspy.core.web.subscription.controller;

import org.xylan.mailspy.core.web.subscription.sse.MailSpySseEmitterRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsSubscriptionControllerTest {

    @Mock
    private MailSpySseEmitterRegistry mailSpySseEmitterRegistry;

    @InjectMocks
    private MailSpySseSubscriptionController underTest;

    @Test
    public void createSseEmitterShouldReturnResultOfSseEmitterRegistry() {
        // GIVEN
        SseEmitter expected = mock(SseEmitter.class);
        given(mailSpySseEmitterRegistry.createEmitter()).willReturn(expected);

        // WHEN
        SseEmitter actual = underTest.createSseEmitter();

        // THEN
        assertEquals(actual, expected);
    }

}
