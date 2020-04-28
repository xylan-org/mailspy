package org.abelk.devmailserver.core.mailserver.convert;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.abelk.devmailserver.core.domain.DmsAttachment;
import org.abelk.devmailserver.core.domain.DmsEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;

@Component
public class EmailConverter {

    private AttachmentConverter attachmentConverter;

    @Autowired
    public void setAttachmentConverter(final AttachmentConverter attachmentConverter) {
        this.attachmentConverter = attachmentConverter;
    }

    public DmsEmail convert(final Email email) {
        final DmsEmail result = new DmsEmail();
        result.setFromSender(email.getFromEmailHeaderValue());
        result.setToRecipients(parseCommaDelimitedString(email.getToEmailHeaderValue()));
        result.setCcRecipients(parseCommaDelimitedString(email.getCCEmailHeaderValue()));
        result.setSubject(email.getEmailSubject());
        result.setAttachments(convertAttachments(email.getAttachments()));
        result.setPlainTextBody(attachmentConverter.convert(email.getPlainTextEmailBody()));
        result.setHtmlBody(attachmentConverter.convert(email.getHTMLEmailBody()));
        result.setCalendarBody(attachmentConverter.convert(email.getCalendarBody()));
        return result;
    }

    private List<DmsAttachment> convertAttachments(final List<Attachment> attachments) {
        return attachments.stream()
                .map(attachmentConverter::convert)
                .collect(toList());
    }

    private static List<String> parseCommaDelimitedString(final String value) {
        return Optional.ofNullable(value)
                .map(v -> Arrays.stream(v.split(",")).map(String::trim).collect(toList()))
                .orElseGet(Collections::emptyList);
    }

}
