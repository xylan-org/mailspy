package org.xylan.mailspy.core.impl.web.history.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsHistoryControllerTest {

    @Mock
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @InjectMocks
    private MailSpyHistoryController underTest;

    @Test
    public void getMailsHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        List<MailSpyEmail> expected = List.of(MailSpyEmail.builder().build());
        given(mailSpyHistoryStorage.getHistory()).willReturn(expected);

        // WHEN
        List<MailSpyEmail> actual = underTest.getMailsHistory();

        // THEN
        assertEquals(actual, expected);
    }

    @Test
    public void clearHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        // WHEN
        underTest.clearHistory();

        // THEN
        then(mailSpyHistoryStorage).should().clearHistory();
    }

}
