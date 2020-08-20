package org.abelk.devmailserver.core.web.sse;

import org.abelk.devmailserver.core.domain.EmailReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SseEmailReceivedListener implements ApplicationListener<EmailReceivedEvent> {

    private SseEmitterRegistry sseEmitterRegistry;

    @Autowired
    public void setSseEmitterRegistry(final SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @Override
    public void onApplicationEvent(final EmailReceivedEvent event) {
        sseEmitterRegistry.broadcast(event.getSource());
    }

}
