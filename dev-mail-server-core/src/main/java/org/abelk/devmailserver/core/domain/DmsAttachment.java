package org.abelk.devmailserver.core.domain;

import lombok.Data;

@Data
public class DmsAttachment {

    private Exception exception;
    private String name;
    private String mimeType;
    private String charset;
    private byte[] bodyRaw;
    private String bodyText;

    public static DmsAttachment ofException(final Exception exception) {
        final DmsAttachment result = new DmsAttachment();
        result.setException(exception);
        return result;
    }

}
