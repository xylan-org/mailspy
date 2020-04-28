package org.abelk.devmailserver.core.mailserver;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

public class EventPublishingMessageHandler implements MessageHandler {

    private ApplicationEventPublisher applicationEventPublisher;
    private EmailParser emailParser;
    private Supplier<ZonedDateTime> dateTimeSupplier = ZonedDateTime::now;

    @Autowired
    public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired
    public void setEmailParser(final EmailParser emailParser) {
        this.emailParser = emailParser;
    }

    public void setDateTimeSupplier(final Supplier<ZonedDateTime> dateTimeSupplier) {
        this.dateTimeSupplier = dateTimeSupplier;
    }

    @Override
    public void data(final InputStream messageStream) throws RejectException, TooMuchDataException, IOException {
        final DmsEmail parseResult = emailParser.parseMessage(messageStream);
        parseResult.setReceivedTimestamp(dateTimeSupplier.get());
        final EmailReceivedEvent emailReceivedEvent = new EmailReceivedEvent(parseResult);
        applicationEventPublisher.publishEvent(emailReceivedEvent);
    }

    @Override
    public void from(final String from) throws RejectException {
        // ignored
    }

    @Override
    public void recipient(final String recipient) throws RejectException {
        // ignored
    }

    @Override
    public void done() {
        // ignored
    }

}
