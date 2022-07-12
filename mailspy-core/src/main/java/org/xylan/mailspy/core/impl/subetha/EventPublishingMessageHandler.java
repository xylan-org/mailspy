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

package org.xylan.mailspy.core.impl.subetha;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.subethamail.smtp.MessageHandler;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail.MailSpyEmailBuilder;

/**
 * MailSpy's implementation of the embedded SubEtha SMTP server's {@link MessageHandler}.
 */
@Slf4j
public class EventPublishingMessageHandler implements MessageHandler {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Setter
    private Supplier<UUID> uuidSupplier = UUID::randomUUID;

    @Setter
    private Supplier<Instant> nowSupplier = Instant::now;

    @Override
    public void data(final InputStream messageStream) {
        MailSpyEmailBuilder builder = MailSpyEmail.builder();
        try {
            builder.rawMessage(IOUtils.toByteArray(messageStream));
        } catch (final IOException exception) {
            builder.exception(exception);
            log.error("Exception thrown while reading mail message.", exception);
        }
        builder.id(uuidSupplier.get().toString()).timestamp(nowSupplier.get());
        applicationEventPublisher.publishEvent(new EmailReceivedEvent(builder.build()));
    }

    @Override
    public void from(final String from) {
        // ignored
    }

    @Override
    public void recipient(final String recipient) {
        // ignored
    }

    @Override
    public void done() {
        // ignored
    }
}
