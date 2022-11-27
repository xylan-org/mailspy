package org.xylan.mailspy.core.impl.web.history.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.xylan.mailspy.core.config.base.MailSpyWebSocketConfig.APPLICATION_DESTINATION_PREFIX;

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
    public void subscribeInboundChannelShouldRegisterHandlerThatSendsHistoryWhenMessageReceivedOnGetHistoryDestination() {
        // GIVEN
        String userId = "1234";
        MailSpyEmail email = new MailSpyEmail();
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            DESTINATION_HEADER, APPLICATION_DESTINATION_PREFIX + "/get-history",
            NATIVE_HEADERS, Map.of("userId", List.of(userId))
        ));

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
    public void subscribeInboundChannelShouldRegisterHandlerThanClearsHistoryAndSendsClearMessageWhenMessageReceivedOnClearHistoryDestination() {
        // GIVEN
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            DESTINATION_HEADER, APPLICATION_DESTINATION_PREFIX + "/clear-history"
        ));

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
        Message<?> message = new GenericMessage<>(new Object(), Map.of(
            DESTINATION_HEADER, APPLICATION_DESTINATION_PREFIX + "/perform-magic"
        ));

        // WHEN
        underTest.subscribeInboundChannel();

        // THEN
        then(inboundChannel).should().subscribe(messageHandlerCaptor.capture());
        messageHandlerCaptor.getValue().handleMessage(message);
        // no exception thrown
    }

}