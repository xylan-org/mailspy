package org.xylan.mailspy.core.impl.web.email;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class MailSpyEmailReceivedEventWebsocketSendingListenerTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private MailSpyEmailReceivedEventWebsocketSendingListener underTest;

    @Test
    public void onApplicationEventShouldSendMessageUsingMessagingTemplate() {
        // GIVEN
        MailSpyEmail email = new MailSpyEmail();
        EmailReceivedEvent event = new EmailReceivedEvent(email);

        // WHEN
        underTest.onApplicationEvent(event);

        // THEN
        then(simpMessagingTemplate).should().convertAndSend("/ws/topic/email", email);
    }

}