package org.xylan.mailspy.core.impl.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

@Controller
public class MailSpyClearedWebSocketSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @MessageMapping("/clear")
    public void clearMails() {
        mailSpyHistoryStorage.clearHistory();
        simpMessagingTemplate.convertAndSend("/ws/topic/clear", "");
    }

}
