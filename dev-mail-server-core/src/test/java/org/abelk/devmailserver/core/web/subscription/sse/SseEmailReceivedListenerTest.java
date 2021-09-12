package org.abelk.devmailserver.core.web.subscription.sse;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class SseEmailReceivedListenerTest {

    @Mock
    private SseEmitterRegistry sseEmitterRegistry;

    @InjectMocks
    private SseEmailReceivedListener underTest;

    @Test
    public void onApplicationEventShouldInvokeBroadcastOnSseEmitterRegistry() {
        // GIVEN
        DmsEmail email = DmsEmail.builder().build();
        EmailReceivedEvent event = new EmailReceivedEvent(email);

        // WHEN
        underTest.onApplicationEvent(event);

        // THEN
        then(sseEmitterRegistry).should().broadcast("mail", email);
    }

}
