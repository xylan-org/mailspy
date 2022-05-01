package org.xylan.mailspy.core.impl.web.history.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

/**
 * Controller for history requests.
 */
@RestController
public class MailSpyHistoryController {

    @Autowired
    private MailSpyHistoryStorage mailsHistoryStorage;

    /**
     * Gets the mail history.
     * @return The mail history.
     */
    @GetMapping(path = "/mails/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MailSpyEmail> getMailsHistory() {
        return mailsHistoryStorage.getHistory();
    }

    /**
     * Clears the mail history.
     */
    @DeleteMapping(path = "/mails/history")
    public void clearHistory() {
        mailsHistoryStorage.clearHistory();
    }

}
