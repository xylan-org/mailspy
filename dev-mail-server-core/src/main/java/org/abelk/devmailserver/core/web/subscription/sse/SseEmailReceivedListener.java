package org.abelk.devmailserver.core.web.subscription.sse;

import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SseEmailReceivedListener implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private SseEmitterRegistry sseEmitterRegistry;

    @Override
    public void onApplicationEvent(final EmailReceivedEvent event) {
        sseEmitterRegistry.broadcast("mail", event.getSource());
    }

}
