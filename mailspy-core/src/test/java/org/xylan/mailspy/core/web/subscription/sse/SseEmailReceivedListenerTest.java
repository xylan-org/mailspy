package org.xylan.mailspy.core.web.subscription.sse;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.domain.MailSpyEmail;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class SseEmailReceivedListenerTest {

    @Mock
    private MailSpySseEmitterRegistry mailSpySseEmitterRegistry;

    @InjectMocks
    private MailSpySseEmailReceivedListener underTest;

    @Test
    public void onApplicationEventShouldInvokeBroadcastOnSseEmitterRegistry() {
        // GIVEN
        MailSpyEmail email = MailSpyEmail.builder().build();
        EmailReceivedEvent event = new EmailReceivedEvent(email);

        // WHEN
        underTest.onApplicationEvent(event);

        // THEN
        then(mailSpySseEmitterRegistry).should().broadcast("mail", email);
    }

}
