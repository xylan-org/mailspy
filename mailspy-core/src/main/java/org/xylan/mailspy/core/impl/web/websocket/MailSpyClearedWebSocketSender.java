package org.xylan.mailspy.core.impl.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.domain.WebSocketMessage;
import org.xylan.mailspy.core.impl.domain.WebSocketMessageType;

@Controller
public class MailSpyClearedWebSocketSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MailSpyProperties properties;

    @MessageMapping("/clear")
    public void clearMails() {
        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
            .type(WebSocketMessageType.CLEARED)
            .build();
        simpMessagingTemplate.convertAndSend(properties.getPathNoTrailingSlash() + "/ws/topic/messages", webSocketMessage);
    }

}
