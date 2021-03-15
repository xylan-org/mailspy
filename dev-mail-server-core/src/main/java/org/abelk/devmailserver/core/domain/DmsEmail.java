package org.abelk.devmailserver.core.domain;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmsEmail {

    private String id;
    private Instant timestamp;
    private Exception exception;
    private byte[] rawMessage;

}
