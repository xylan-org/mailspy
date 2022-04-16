package org.xylan.mailspy.core.impl.web.history.storage;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class MailSpyEmailReceivedListenerTest {

    @Mock
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @InjectMocks
    private MailSpyEmailReceivedListener underTest;

    @Test
    public void onApplicationEventShouldAddEmailToMailsHistoryStorage() {
        // GIVEN
        MailSpyEmail email = MailSpyEmail.builder().build();
        EmailReceivedEvent event = new EmailReceivedEvent(email);

        // WHEN
        underTest.onApplicationEvent(event);

        // THEN
        then(mailSpyHistoryStorage).should().addEmail(email);
    }

}
