package org.abelk.devmailserver.core.web.history.controller;

import java.util.List;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.web.history.storage.MailsHistoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailsHistoryController {

    @Autowired
    private MailsHistoryStorage mailsHistoryStorage;

    @GetMapping(path = "/mails/history")
    public List<DmsEmail> getMailsHistory() {
        return mailsHistoryStorage.getHistory();
    }

    @DeleteMapping(path = "/mails/history")
    public void clearHistory() {
        mailsHistoryStorage.clearHistory();
    }

}
