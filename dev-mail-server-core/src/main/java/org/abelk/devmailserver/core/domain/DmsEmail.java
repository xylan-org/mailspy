package org.abelk.devmailserver.core.domain;

import lombok.Getter;

@Getter
public class DmsEmail {

    private Exception exception;
    private byte[] rawMessage;

    public static DmsEmail ofException(final Exception exception) {
        final DmsEmail result = new DmsEmail();
        result.exception = exception;
        result.rawMessage = new byte[] {};
        return result;
    }

    public static DmsEmail ofRawMessage(final byte[] rawMessage) {
        final DmsEmail result = new DmsEmail();
        result.rawMessage = rawMessage;
        return result;
    }

}
