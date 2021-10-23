package org.xylan.mailspy.core.web.history.storage;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.domain.MailSpyEmail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class MailSpyHistoryStorage {

    @Setter
    @Autowired
    private MailSpyProperties properties;

    private final Queue<MailSpyEmail> mailQueue = new LinkedList<>();

    public void addEmail(final MailSpyEmail mail) {
        final int limit = properties.getRetainEmails();
        synchronized (mailQueue) {
            mailQueue.add(mail);
            if (mailQueue.size() > limit) {
                mailQueue.remove();
            }
        }
    }

    public List<MailSpyEmail> getHistory() {
        synchronized (mailQueue) {
            return new ArrayList<>(mailQueue);
        }
    }

    public void clearHistory() {
        synchronized (mailQueue) {
            mailQueue.clear();
        }
    }

}
