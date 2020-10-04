package org.abelk.devmailserver.core.domain;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class EmailReceivedEvent extends ApplicationEvent {

    public EmailReceivedEvent(final DmsEmail source) {
        super(source);
    }

    @Override
    public DmsEmail getSource() {
        return (DmsEmail) source;
    }

}
