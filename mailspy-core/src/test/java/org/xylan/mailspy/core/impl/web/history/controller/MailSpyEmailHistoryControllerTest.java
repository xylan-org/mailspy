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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.xylan.mailspy.core.config.base.MailSpyWebSocketConfig.APPLICATION_DESTINATION_PREFIX;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;
import org.xylan.mailspy.core.impl.ws.NativeHeaderExtractor;

@Listeners(MockitoTestNGListener.class)
public class MailSpyEmailHistoryControllerTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private AbstractSubscribableChannel inboundChannel;

    @Mock
    private NativeHeaderExtractor nativeHeaderExtractor;

    @Mock
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @Captor
    private ArgumentCaptor<MessageHandler> messageHandlerCaptor;

    @InjectMocks
    private MailSpyEmailHistoryController underTest;

    @Test
    public void
            subscribeInboundChannelShouldRegisterHandlerThatSendsHistoryWhenMessageReceivedOnGetHistoryDestination() {
        // GIVEN
        String userId = "1234";
        MailSpyEmail email = new MailSpyEmail();
        Message<?> message = new GenericMessage<>(
                new Object(),
                Map.of(
                        DESTINATION_HEADER,
                        APPLICATION_DESTINATION_PREFIX + "/get-history",
                        NATIVE_HEADERS,
                        Map.of("userId", List.of(userId))));

        given(nativeHeaderExtractor.getHeader(message, "userId")).willReturn(userId);
        given(mailSpyHistoryStorage.getHistory()).willReturn(List.of(email));

        // WHEN
        underTest.subscribeInboundChannel();

        // THEN
        then(inboundChannel).should().subscribe(messageHandlerCaptor.capture());
        messageHandlerCaptor.getValue().handleMessage(message);
        then(simpMessagingTemplate).should().convertAndSendToUser(userId, "/history", email);
    }

    @Test
    public void
            subscribeInboundChannelShouldRegisterHandlerThanClearsHistoryAndSendsClearMessageWhenMessageReceivedOnClearHistoryDestination() {
        // GIVEN
        Message<?> message = new GenericMessage<>(
                new Object(), Map.of(DESTINATION_HEADER, APPLICATION_DESTINATION_PREFIX + "/clear-history"));

        // WHEN
        underTest.subscribeInboundChannel();

        // THEN
        then(inboundChannel).should().subscribe(messageHandlerCaptor.capture());
        messageHandlerCaptor.getValue().handleMessage(message);
        then(mailSpyHistoryStorage).should().clearHistory();
        then(simpMessagingTemplate).should().convertAndSend("/ws/topic/clear", "");
    }

    @Test
    public void subscribeInboundChannelShouldIgnoreNullDestination() {
        // GIVEN
        Message<?> message = new GenericMessage<>(new Object(), Collections.emptyMap());

        // WHEN
        underTest.subscribeInboundChannel();

        // THEN
        then(inboundChannel).should().subscribe(messageHandlerCaptor.capture());
        messageHandlerCaptor.getValue().handleMessage(message);
        // no exception thrown
    }

    @Test
    public void subscribeInboundChannelShouldIgnoreUnsupportedDestination() {
        // GIVEN
        Message<?> message = new GenericMessage<>(
                new Object(), Map.of(DESTINATION_HEADER, APPLICATION_DESTINATION_PREFIX + "/perform-magic"));

        // WHEN
        underTest.subscribeInboundChannel();

        // THEN
        then(inboundChannel).should().subscribe(messageHandlerCaptor.capture());
        messageHandlerCaptor.getValue().handleMessage(message);
        // no exception thrown
    }
}
