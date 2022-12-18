package org.xylan.mailspy.integration.ws;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
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
                    ws.awaitMessage();
                    ws.awaitMessage();

                    ws.sendMessage(Map.of(
                        DESTINATION_HEADER, "/ws/dest/get-history",
                        NATIVE_HEADERS, Map.of("userId", List.of("123"))));

                    // WHEN
                    Message<?> message1 = ws.awaitMessage();
                    Message<?> message2 = ws.awaitMessage();

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
