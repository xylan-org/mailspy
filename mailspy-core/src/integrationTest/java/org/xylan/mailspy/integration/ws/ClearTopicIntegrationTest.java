package org.xylan.mailspy.integration.ws;

import java.util.Collections;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;

public class ClearTopicIntegrationTest extends BaseIntegrationTest {

    @Test
    public void clearHistoryTopicShouldReceiveClearMessageWhenClearMessageIsSentToClearHistoryDestination() {
        runWithWs((context, ws) -> {
            // GIVEN
            ws.simulateMessageReceived(Map.of(
                DESTINATION_HEADER, "/ws/dest/clear-history",
                MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                SESSION_ID_HEADER, "sessionId",
                SESSION_ATTRIBUTES, Collections.emptyMap()));

            // WHEN
            Message<?> message = ws.awaitMessageSent();

            // THEN
            assertThat(message, messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/clear")));
        });
    }

}
