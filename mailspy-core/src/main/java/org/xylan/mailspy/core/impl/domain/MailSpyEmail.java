package org.xylan.mailspy.core.impl.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
