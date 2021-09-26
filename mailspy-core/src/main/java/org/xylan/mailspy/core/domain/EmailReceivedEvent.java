package org.xylan.mailspy.core.domain;

import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
@EqualsAndHashCode(of = "source")
public class EmailReceivedEvent extends ApplicationEvent {

    public EmailReceivedEvent(final MailSpyEmail source) {
        super(source);
    }

    @Override
    public MailSpyEmail getSource() {
        return (MailSpyEmail) source;
    }

}
