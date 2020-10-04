package org.abelk.devmailserver.core.subetha;

import java.io.IOException;
import java.io.InputStream;

import org.abelk.devmailserver.core.domain.DmsEmail;
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

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void data(final InputStream messageStream) throws RejectException, TooMuchDataException, IOException {
        DmsEmail result;
        try {
            result = DmsEmail.ofRawMessage(IOUtils.toByteArray(messageStream));
        } catch (final IOException exception) {
            result = DmsEmail.ofException(exception);
            log.error("Exception thrown while reading mail message.", exception);
        }
        applicationEventPublisher.publishEvent(new EmailReceivedEvent(result));
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
