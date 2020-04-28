package org.abelk.devmailserver.core.web.sse;

import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseSubscriptionController {

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
