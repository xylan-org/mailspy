package org.xylan.mailspy.integration.ws;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.util.MimeType;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.MessageHeaders.CONTENT_TYPE;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messagePayloadMatches;

public class HistoryTopicIntegrationTest extends BaseIntegrationTest {

    @Test
    public void userEmailHistoryTopicShouldReceiveEmailHistoryWhenUserSendsMessageToGetHistoryDestination() {
        runWithWs(
            (context, ws) -> {
                // GIVEN
                context.publishEvent(new EmailReceivedEvent(MailSpyEmail.builder()
                    .id("mailId1")
                    .build()));
                context.publishEvent(new EmailReceivedEvent(MailSpyEmail.builder()
                    .id("mailId2")
                    .build()));

                // consume messages on /ws/topic/email
                ws.awaitMessageSent();
                ws.awaitMessageSent();

                ws.simulateMessageReceived(Map.of(
                    DESTINATION_HEADER, "/ws/dest/get-history",
                    NATIVE_HEADERS, Map.of("userId", List.of("123"),
                    MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                    SESSION_ID_HEADER, "sessionId",
                    SESSION_ATTRIBUTES, Collections.emptyMap())));

                // WHEN
                Message<?> message1 = ws.awaitMessageSent();
                Message<?> message2 = ws.awaitMessageSent();

                // THEN
                assertThat(message1, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/user/123/history")),
                    messageHeaderMatches(CONTENT_TYPE, equalTo(MimeType.valueOf("application/json"))),
                    messagePayloadMatches(jsonPathMatches("$.id", equalTo("mailId1")))));
                assertThat(message2, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/user/123/history")),
                    messageHeaderMatches(CONTENT_TYPE, equalTo(MimeType.valueOf("application/json"))),
                    messagePayloadMatches(jsonPathMatches("$.id", equalTo("mailId2")))));
            });
    }

}
