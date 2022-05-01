package org.xylan.mailspy.core.impl.domain;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of an email received by MailSpy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSpyEmail {

    private String id;
    private Instant timestamp;
    private Exception exception;
    private byte[] rawMessage;

}
