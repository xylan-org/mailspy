package org.xylan.mailspy.core.impl.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

@Controller
public class MailSpyEmailSubscriptionController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @MessageMapping("/history")
    public void getHistory(@Header("userId") String userId) {
        mailSpyHistoryStorage.getHistory()
            .forEach(email -> simpMessagingTemplate.convertAndSendToUser(userId, "/history", email));
    }

}
