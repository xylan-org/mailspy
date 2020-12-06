package org.abelk.devmailserver.core.web.history.storage;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.abelk.devmailserver.core.domain.DmsEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailsHistoryStorage {

    @Autowired
    private DevMailServerProperties properties;

    private final Queue<DmsEmail> mailQueue = new LinkedList<>();

    public void addEmail(final DmsEmail mail) {
        final int limit = properties.getRetainEmails();
        synchronized (mailQueue) {
            mailQueue.add(mail);
            if (mailQueue.size() > limit) {
                mailQueue.remove();
            }
        }
    }

    public List<DmsEmail> getHistory() {
        synchronized (mailQueue) {
            return mailQueue.stream()
                    .collect(Collectors.toList());
        }
    }

    public void clearHistory() {
        synchronized (mailQueue) {
            mailQueue.clear();
        }
    }

}
