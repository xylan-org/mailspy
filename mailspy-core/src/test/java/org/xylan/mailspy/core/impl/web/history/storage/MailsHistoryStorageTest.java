package org.xylan.mailspy.core.impl.web.history.storage;

import org.mockito.InjectMocks;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsHistoryStorageTest {

    @InjectMocks
    private MailSpyHistoryStorage underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    public void addEmailShouldAddEmailToQueueWhenLimitIsNotYetReached() {
        // GIVEN
        MailSpyEmail email = MailSpyEmail.builder().id("id").build();

        // WHEN
        underTest.addEmail(email);

        // THEN
        assertEquals(underTest.getHistory().get(0), email);
    }

    @Test
    public void addEmailShouldAddEmailToQueueAndRemoveFirstWhenLimitIsReached() {
        // GIVEN
        MailSpyEmail email1 = MailSpyEmail.builder().id("uno").build();
        MailSpyEmail email2 = MailSpyEmail.builder().id("dos").build();
        MailSpyEmail email3 = MailSpyEmail.builder().id("tres").build();
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
        MailSpyEmail email = MailSpyEmail.builder().id("id").build();
        underTest.addEmail(email);

        // WHEN
        underTest.clearHistory();

        // THEN
        assertEquals(underTest.getHistory(), Collections.emptyList());
    }

    private MailSpyProperties createProperties() {
        MailSpyProperties properties = new MailSpyProperties();
        properties.setRetainEmails(2);
        return properties;
    }

}
