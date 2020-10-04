package org.abelk.devmailserver.core.web.history.storage;

import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StoringEmailReceivedListener implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private MailHistoryStorage mailHistoryStorage;

    @Override
    public void onApplicationEvent(final EmailReceivedEvent event) {
        mailHistoryStorage.addEmail(event.getSource());
    }

}
