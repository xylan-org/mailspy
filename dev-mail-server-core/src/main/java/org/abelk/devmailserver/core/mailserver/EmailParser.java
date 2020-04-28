package org.abelk.devmailserver.core.mailserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.abelk.devmailserver.core.domain.DmsEmail;
import org.abelk.devmailserver.core.mailserver.convert.EmailConverter;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

@Component
public class EmailParser {

    private MimeStreamParser mime4jParser;
    private CustomContentHandler contentHandler;
    private EmailConverter emailConverter;

    @Autowired
    public void setMime4jParser(final MimeStreamParser mime4jParser) {
        this.mime4jParser = mime4jParser;
    }

    @Autowired
    public void setContentHandler(final CustomContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Autowired
    public void setEmailConverter(final EmailConverter emailConverter) {
        this.emailConverter = emailConverter;
    }

    public DmsEmail parseMessage(final InputStream messageStream) {
        DmsEmail result;
        try {
            final byte[] rawMessage = IOUtils.toByteArray(messageStream);
            mime4jParser.parse(new ByteArrayInputStream(rawMessage));
            final Email email = contentHandler.getEmail();
            result = emailConverter.convert(email);
            result.setRawMessage(rawMessage);
        } catch (MimeException | IOException exception) {
            result = DmsEmail.ofException(exception);
        }
        return result;
    }

}
