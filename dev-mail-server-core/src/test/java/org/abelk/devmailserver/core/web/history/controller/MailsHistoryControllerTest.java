package org.abelk.devmailserver.core.web.history.controller;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.web.history.storage.MailsHistoryStorage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailsHistoryControllerTest {

    @Mock
    private MailsHistoryStorage mailsHistoryStorage;

    @InjectMocks
    private MailsHistoryController underTest;

    @Test
    public void getMailsHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        List<DmsEmail> expected = List.of(DmsEmail.builder().build());
        given(mailsHistoryStorage.getHistory()).willReturn(expected);

        // WHEN
        List<DmsEmail> actual = underTest.getMailsHistory();

        // THEN
        assertEquals(actual, expected);
    }

    @Test
    public void clearHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        // WHEN
        underTest.clearHistory();

        // THEN
        then(mailsHistoryStorage).should().clearHistory();
    }

}
