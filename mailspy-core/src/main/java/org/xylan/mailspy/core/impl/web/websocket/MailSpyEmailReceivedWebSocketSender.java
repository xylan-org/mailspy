package org.xylan.mailspy.core.impl.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;

@Component
public class MailSpyEmailReceivedWebSocketSender implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onApplicationEvent(EmailReceivedEvent event) {
        simpMessagingTemplate.convertAndSend("/ws/topic/email", event.getSource());
    }

}
