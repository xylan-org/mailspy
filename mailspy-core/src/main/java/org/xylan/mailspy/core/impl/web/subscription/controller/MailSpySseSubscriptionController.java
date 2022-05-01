package org.xylan.mailspy.core.impl.web.subscription.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xylan.mailspy.core.impl.web.subscription.sse.MailSpySseEmitterRegistry;

/**
 * Controller for SSE subscriptions.
 */
@Controller
public class MailSpySseSubscriptionController {

    @Autowired
    private MailSpySseEmitterRegistry sseEmitterRegistry;

    /**
     * Subscribes the client for Server-Sent Events.
     * @return The constructed {@link SseEmitter}.
     */
    @RequestMapping(path = "/mails/subscribe")
    public SseEmitter createSseEmitter() {
        return sseEmitterRegistry.createEmitter();
    }

}
