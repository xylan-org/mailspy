package org.abelk.devmailserver.core.web.history.controller;

import java.util.List;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.web.history.storage.MailHistoryStorage;
import org.abelk.devmailserver.core.web.support.handlermapping.HandlerMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailsHistoryController {

    @Autowired
    private MailHistoryStorage mailHistoryStorage;

    @HandlerMethod
    public List<DmsEmail> getMailsHistory() {
        return mailHistoryStorage.getHistory();
    }

}
