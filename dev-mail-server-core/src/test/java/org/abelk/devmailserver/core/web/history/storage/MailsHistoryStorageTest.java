package org.abelk.devmailserver.core.web.history.storage;

import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.abelk.devmailserver.core.domain.DmsEmail;
import org.mockito.InjectMocks;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsHistoryStorageTest {

    @InjectMocks
    private MailsHistoryStorage underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    public void addEmailShouldAddEmailToQueueWhenLimitIsNotYetReached() {
        // GIVEN
        DmsEmail email = DmsEmail.builder().id("id").build();

        // WHEN
        underTest.addEmail(email);

        // THEN
        assertEquals(underTest.getHistory().get(0), email);
    }

    @Test
    public void addEmailShouldAddEmailToQueueAndRemoveFirstWhenLimitIsReached() {
        // GIVEN
        DmsEmail email1 = DmsEmail.builder().id("uno").build();
        DmsEmail email2 = DmsEmail.builder().id("dos").build();
        DmsEmail email3 = DmsEmail.builder().id("tres").build();
        underTest.addEmail(email1);
        underTest.addEmail(email2);

        // WHEN
        underTest.addEmail(email3);

        // THEN
        assertEquals(underTest.getHistory(), List.of(email2, email3));
    }

    @Test
    public void clearHistoryShouldClearQueue() {
        // GIVEN
        DmsEmail email = DmsEmail.builder().id("id").build();
        underTest.addEmail(email);

        // WHEN
        underTest.clearHistory();

        // THEN
        assertEquals(underTest.getHistory(), Collections.emptyList());
    }

    private DevMailServerProperties createProperties() {
        DevMailServerProperties properties = new DevMailServerProperties();
        properties.setRetainEmails(2);
        return properties;
    }

}
