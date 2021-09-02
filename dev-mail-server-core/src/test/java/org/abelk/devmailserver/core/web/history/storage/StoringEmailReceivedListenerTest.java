package org.abelk.devmailserver.core.web.history.storage;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class StoringEmailReceivedListenerTest {

    @Mock
    private MailsHistoryStorage mailsHistoryStorage;

    @InjectMocks
    private StoringEmailReceivedListener underTest;

    @Test
    public void onApplicationEventShouldAddEmailToMailsHistoryStorage() {
        // GIVEN
        DmsEmail email = DmsEmail.builder().build();
        EmailReceivedEvent event = new EmailReceivedEvent(email);

        // WHEN
        underTest.onApplicationEvent(event);

        // THEN
        then(mailsHistoryStorage).should().addEmail(email);
    }

}
