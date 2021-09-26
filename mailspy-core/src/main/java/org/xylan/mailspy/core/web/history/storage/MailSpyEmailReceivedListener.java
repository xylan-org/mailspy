package org.xylan.mailspy.core.web.history.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.domain.EmailReceivedEvent;

@Component
public class MailSpyEmailReceivedListener implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private MailSpyHistoryStorage mailsHistoryStorage;

    @Override
    public void onApplicationEvent(final EmailReceivedEvent event) {
        mailsHistoryStorage.addEmail(event.getSource());
    }

}
