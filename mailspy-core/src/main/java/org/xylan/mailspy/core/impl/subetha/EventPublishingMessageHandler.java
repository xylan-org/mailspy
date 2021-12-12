package org.xylan.mailspy.core.impl.subetha;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.subethamail.smtp.MessageHandler;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail.MailSpyEmailBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

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
        builder.id(uuidSupplier.get().toString())
            .timestamp(nowSupplier.get());
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
