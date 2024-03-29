/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
