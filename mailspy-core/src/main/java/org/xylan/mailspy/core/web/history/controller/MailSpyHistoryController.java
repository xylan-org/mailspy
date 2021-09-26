package org.xylan.mailspy.core.web.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xylan.mailspy.core.domain.MailSpyEmail;
import org.xylan.mailspy.core.web.history.storage.MailSpyHistoryStorage;

import java.util.List;

@RestController
public class MailSpyHistoryController {

    @Autowired
    private MailSpyHistoryStorage mailsHistoryStorage;

    @GetMapping(path = "/mails/history")
    public List<MailSpyEmail> getMailsHistory() {
        return mailsHistoryStorage.getHistory();
    }

    @DeleteMapping(path = "/mails/history")
    public void clearHistory() {
        mailsHistoryStorage.clearHistory();
    }

}
