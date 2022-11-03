package org.xylan.mailspy.core.impl.web.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

@Controller
public class MailSpyEmailHistoryController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @MessageMapping("/get-history")
    public void getHistory(@Header("userId") String userId) {
        mailSpyHistoryStorage.getHistory()
            .forEach(email -> simpMessagingTemplate.convertAndSendToUser(userId, "/history", email));
    }

    @MessageMapping("/clear-history")
    public void clearMails() {
        mailSpyHistoryStorage.clearHistory();
        simpMessagingTemplate.convertAndSend("/ws/topic/clear", "");
    }

}
