/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.core.impl.web.history.controller;

import static org.xylan.mailspy.core.config.base.MailSpyWebSocketConfig.APPLICATION_DESTINATION_PREFIX;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.stereotype.Controller;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;
import org.xylan.mailspy.core.impl.ws.NativeHeaderExtractor;

/**
 * Controller for MailSpy's email history WebSocket topics.
 */
@Controller
public class MailSpyEmailHistoryController {

    @Autowired
    @Qualifier("mailSpyBrokerMessagingTemplate")
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    @Qualifier("mailSpyClientInboundChannel")
    private AbstractSubscribableChannel inboundChannel;

    @Autowired
    private NativeHeaderExtractor nativeHeaderExtractor;

    @Autowired
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    /**
     * Subscribes on the email history topics, and registers reply actions.
     */
    @PostConstruct
    public void subscribeInboundChannel() {
        inboundChannel.subscribe((Message<?> message) -> {
            String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
            if (destination != null) {
                switch (destination) {
                    case APPLICATION_DESTINATION_PREFIX + "/get-history":
                        String userId = nativeHeaderExtractor.getHeader(message, "userId");
                        sendHistory(userId);
                        break;
                    case APPLICATION_DESTINATION_PREFIX + "/clear-history":
                        clearHistory();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void sendHistory(String userId) {
        mailSpyHistoryStorage
                .getHistory()
                .forEach(email -> simpMessagingTemplate.convertAndSendToUser(userId, "/history", email));
    }

    private void clearHistory() {
        mailSpyHistoryStorage.clearHistory();
        simpMessagingTemplate.convertAndSend("/ws/topic/clear", "");
    }
}
