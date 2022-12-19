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

package org.xylan.mailspy.integration.ws;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.MessageHeaders.CONTENT_TYPE;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messagePayloadMatches;

import org.springframework.messaging.Message;
import org.springframework.util.MimeType;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

public class EmailTopicIntegrationTest extends BaseIntegrationTest {

    @Test
    public void emailTopicShouldReceiveReceivedEmail() {
        runWithWs((context, ws) -> {
            // GIVEN
            context.publishEvent(new EmailReceivedEvent(MailSpyEmail.builder()
                    .id("mailId")
                    .timestamp("mailTimeStamp")
                    .rawMessage(new byte[] {0xd, 0xe, 0xa, 0xd, 0xb, 0xe, 0xe, 0xf})
                    .build()));

            // WHEN
            Message<?> message = ws.awaitMessageSent();

            // THEN
            assertThat(
                    message,
                    allOf(
                            messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/email")),
                            messageHeaderMatches(CONTENT_TYPE, equalTo(MimeType.valueOf("application/json"))),
                            messagePayloadMatches(allOf(
                                    jsonPathMatches("$.id", equalTo("mailId")),
                                    jsonPathMatches("$.timestamp", equalTo("mailTimeStamp")),
                                    jsonPathMatches("$.rawMessage", equalTo("DQ4KDQsODg8="))))));
        });
    }
}
