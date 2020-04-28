package org.abelk.devmailserver.core.domain;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class DmsEmail {

    private Exception exception;
    private List<String> toRecipients;
    private List<String> ccRecipients;
    private String fromSender;
    private String subject;
    private List<DmsAttachment> attachments;
    private DmsAttachment plainTextBody;
    private DmsAttachment htmlBody;
    private DmsAttachment calendarBody;
    private ZonedDateTime receivedTimestamp;
    private byte[] rawMessage;

    public static DmsEmail ofException(final Exception exception) {
        final DmsEmail result = new DmsEmail();
        result.setException(exception);
        return result;
    }

}
