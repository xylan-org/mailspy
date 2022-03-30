package org.xylan.mailspy.core.impl.domain;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class EmailReceivedEvent extends ApplicationEvent {

    public EmailReceivedEvent(final MailSpyEmail source) {
        super(source);
    }

    @Override
    public MailSpyEmail getSource() {
        return (MailSpyEmail) source;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmailReceivedEvent && Objects.equals(((EmailReceivedEvent) obj).source, source);
    }

}
