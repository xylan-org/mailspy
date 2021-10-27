package org.xylan.mailspy.core.impl.web.subscription.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;

@Component
public class MailSpySseEmailReceivedListener implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private MailSpySseEmitterRegistry sseEmitterRegistry;

    @Override
    public void onApplicationEvent(final EmailReceivedEvent event) {
        sseEmitterRegistry.broadcast("mail", event.getSource());
    }

}
