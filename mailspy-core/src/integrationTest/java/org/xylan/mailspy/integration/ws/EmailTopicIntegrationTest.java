package org.xylan.mailspy.integration.ws;

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
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messagePayloadMatches;

public class EmailTopicIntegrationTest extends BaseIntegrationTest {

    @Test
    public void emailTopicShouldReceiveReceivedEmail() {
        runWithWs(
            (context, ws) -> {
                // GIVEN
                context.publishEvent(new EmailReceivedEvent(MailSpyEmail.builder()
                    .id("mailId")
                    .timestamp("mailTimeStamp")
                    .rawMessage(new byte[] { 0xd, 0xe, 0xa, 0xd, 0xb, 0xe, 0xe, 0xf })
                    .build()));

                // WHEN
                Message<?> message = ws.awaitMessage();

                // THEN
                assertThat(message, messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/email")));
                assertThat(message, messageHeaderMatches(CONTENT_TYPE, equalTo(MimeType.valueOf("application/json"))));
                assertThat(message, messagePayloadMatches(allOf(
                        jsonPathMatches("$.id", equalTo("mailId")),
                        jsonPathMatches("$.timestamp", equalTo("mailTimeStamp")),
                        jsonPathMatches("$.rawMessage", equalTo("DQ4KDQsODg8=")))));
            });
    }

}
