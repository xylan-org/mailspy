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

package org.xylan.mailspy.integration.endpoint.history;

import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;
import org.xylan.mailspy.integration.common.matchers.MailSpyMatchers;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailTextMatches;

public class GetHistoryIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_RECIPIENT = "test@example.com";
    private static final String TEST_MESSAGE = "Test Message";

    @Test
    public void getShouldReturnEmptyResultWhenNoEmailsReceived() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy/mails/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
        });
    }

    @Test
    public void getShouldReturnEmailWhenHasReceivedOne() {
        run((context, mockMvc) -> {
            sendTestEmail(context);
            mockMvc.perform(get("/mailspy/mails/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", MailSpyMatchers.isValidUuid()))
                .andExpect(jsonPath("$[0].exception", nullValue()))
                .andExpect(jsonPath("$[0].rawMessage", emailHeaderMatches("To", equalTo(TEST_RECIPIENT))))
                .andExpect(jsonPath("$[0].rawMessage", emailTextMatches(equalTo(TEST_MESSAGE))));
        });
    }

    @Test
    public void getShouldNotReturnEmailsExceedingRetentionLimit() {
        String testMessage1 = "Test Message #1";
        String testMessage2 = "Test Message #2";
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.retain-emails=1"),
            (context, mockMvc) -> {
                sendTestEmailWithText(context, testMessage1);
                sendTestEmailWithText(context, testMessage2);
                mockMvc.perform(get("/mailspy/mails/history")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].rawMessage", emailTextMatches(equalTo(testMessage2))));
            });
    }

    @Test
    public void getShouldBeAvailableWithCustomPrefixWhenPrefixIsOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/custom"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/custom/mails/history"))
                    .andExpect(status().isOk());
            });
    }

    private void sendTestEmail(WebApplicationContext context) {
        sendTestEmailWithText(context, TEST_MESSAGE);
    }

    private void sendTestEmailWithText(WebApplicationContext context, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(text);
        message.setTo(TEST_RECIPIENT);
        context.getBean(MailSender.class)
                .send(message);
    }

}
