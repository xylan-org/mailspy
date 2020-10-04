package org.abelk.devmailserver.core.web.subscription.controller;

import org.abelk.devmailserver.core.web.subscription.sse.SseEmitterRegistry;
import org.abelk.devmailserver.core.web.support.handlermapping.HandlerMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class MailsSubscriptionController {

    private SseEmitterRegistry sseEmitterRegistry;

    @Autowired
    public void setSseEmitterRegistry(final SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @HandlerMethod
    public SseEmitter createSseEmitter() {
        return sseEmitterRegistry.createEmitter();
    }

}
