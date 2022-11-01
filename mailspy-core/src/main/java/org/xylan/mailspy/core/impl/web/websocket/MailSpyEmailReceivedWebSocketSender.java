package org.xylan.mailspy.core.impl.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.WebSocketMessage;
import org.xylan.mailspy.core.impl.domain.WebSocketMessageType;

@Component
public class MailSpyEmailReceivedWebSocketSender implements ApplicationListener<EmailReceivedEvent> {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MailSpyProperties properties;

    @Override
    public void onApplicationEvent(EmailReceivedEvent event) {
        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
            .type(WebSocketMessageType.EMAIL_RECEIVED)
            .payload(event.getSource())
            .build();
        simpMessagingTemplate.convertAndSend(properties.getPathNoTrailingSlash() + "/ws/topic/messages", webSocketMessage);
    }

}
