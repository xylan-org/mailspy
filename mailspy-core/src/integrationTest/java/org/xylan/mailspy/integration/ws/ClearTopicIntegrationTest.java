package org.xylan.mailspy.integration.ws;

import java.util.Map;

import org.springframework.messaging.Message;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;

public class ClearTopicIntegrationTest extends BaseIntegrationTest {

    @Test
    public void clearHistoryTopicShouldReceiveClearMessageWhenClearMessageIsSentToClearHistoryDestination() {
        runWithWs((context, ws) -> {
            // GIVEN
            ws.sendMessage(Map.of(DESTINATION_HEADER, "/ws/dest/clear-history"));

            // WHEN
            Message<?> message = ws.awaitMessage();

            // THEN
            assertThat(message, messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/clear")));
        });
    }

}
