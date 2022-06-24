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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClearHistoryIntegrationTest extends BaseIntegrationTest {

    public static final String HISTORY_ENDPOINT = "/mailspy/mails/history";

    @Test
    public void deleteShouldClearHistory() {
        run((context, mockMvc) -> {
            // GIVEN
            sendTestMail(context);
            mockMvc.perform(get(HISTORY_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

            // WHEN
            mockMvc.perform(delete(HISTORY_ENDPOINT))
                .andExpect(status().isOk());

            // THEN
            mockMvc.perform(get(HISTORY_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
        });
    }

    @Test
    public void deleteShouldBeAvailableWithCustomPrefixWhenPrefixIsOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/custom"),
            (context, mockMvc) -> {
                mockMvc.perform(delete("/custom/mails/history"))
                    .andExpect(status().isOk());
            });
    }

    private void sendTestMail(WebApplicationContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("Test Message");
        message.setTo("test@example.com");
        context.getBean(MailSender.class)
            .send(message);
    }

}
