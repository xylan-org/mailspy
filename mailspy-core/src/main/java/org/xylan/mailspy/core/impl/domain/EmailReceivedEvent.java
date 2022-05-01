package org.xylan.mailspy.core.impl.domain;

import java.util.Objects;

import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} that is fired when an email is received by MailSpy.
 */
public class EmailReceivedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4182099155575702359L;

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
