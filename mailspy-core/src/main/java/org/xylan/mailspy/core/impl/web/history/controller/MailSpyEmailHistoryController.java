package org.xylan.mailspy.core.impl.web.history.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

import static org.xylan.mailspy.core.config.base.MailSpyWebSocketConfig.APPLICATION_DESTINATION_PREFIX;

@Controller
public class MailSpyEmailHistoryController {

    @Autowired
    @Qualifier("mailSpyBrokerMessagingTemplate")
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    @Qualifier("mailSpyClientInboundChannel")
    private AbstractSubscribableChannel inboundChannel;

    @Autowired
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @PostConstruct
    public void subscribeInboundChannel() {
        inboundChannel.subscribe((Message<?> message) -> {
            String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
            if (destination != null) {
                switch (destination) {
                    case APPLICATION_DESTINATION_PREFIX + "/get-history":
                        String userId = getUserIdHeader(message);
                        getHistory(userId);
                        break;
                    case APPLICATION_DESTINATION_PREFIX + "/clear-history":
                        clearHistory();
                        break;
                }
            }
        });
    }

    private String getUserIdHeader(Message<?> message) {
        Map<String, List<String>> nativeHeaders = getNativeHeaders(message);
        return Optional.ofNullable(nativeHeaders.get("userId"))
            .filter(list -> !list.isEmpty())
            .map(item -> item.get(0))
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getNativeHeaders(Message<?> message) {
        return (Map<String, List<String>>) message.getHeaders()
            .get(NativeMessageHeaderAccessor.NATIVE_HEADERS);
    }

    private void getHistory(String userId) {
        mailSpyHistoryStorage.getHistory()
            .forEach(email -> simpMessagingTemplate.convertAndSendToUser(userId, "/history", email));
    }

    private void clearHistory() {
        mailSpyHistoryStorage.clearHistory();
        simpMessagingTemplate.convertAndSend("/ws/topic/clear", "");
    }

}
