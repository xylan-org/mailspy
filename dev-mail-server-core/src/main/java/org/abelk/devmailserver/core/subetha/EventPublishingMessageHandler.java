package org.abelk.devmailserver.core.subetha;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

import lombok.Setter;
import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.domain.DmsEmail.DmsEmailBuilder;
import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

import lombok.extern.slf4j.Slf4j;

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
        DmsEmailBuilder builder = DmsEmail.builder();
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
