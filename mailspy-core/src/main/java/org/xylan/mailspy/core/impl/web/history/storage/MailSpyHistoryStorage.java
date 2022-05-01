package org.xylan.mailspy.core.impl.web.history.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

/**
 * Storage for email messages. Retains as many email as defined in configuration.
 */
@Component
public class MailSpyHistoryStorage {

    @Setter
    @Autowired
    private MailSpyProperties properties;

    private final Queue<MailSpyEmail> mailQueue = new LinkedList<>();

    /**
     * Adds an email message to the storage.
     * @param mail The email to add.
     */
    public void addEmail(final MailSpyEmail mail) {
        final int limit = properties.getRetainEmails();
        synchronized (mailQueue) {
            mailQueue.add(mail);
            if (mailQueue.size() > limit) {
                mailQueue.remove();
            }
        }
    }

    /**
     * Gets the email message history.
     * @return The history.
     */
    public List<MailSpyEmail> getHistory() {
        synchronized (mailQueue) {
            return new ArrayList<>(mailQueue);
        }
    }

    /**
     * Clears the email message history.
     */
    public void clearHistory() {
        synchronized (mailQueue) {
            mailQueue.clear();
        }
    }

}
