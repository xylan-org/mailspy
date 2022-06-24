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

package org.xylan.mailspy.integration.smtp;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailHeaderMatches;

public class SmtpServerIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_RECIPIENT = "test@example.com";

    public static class TestSmtpConfig {
        @Bean
        public JavaMailSenderImpl testMailSender() {
            final JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("127.0.0.2");
            sender.setPort(2526);
            return sender;
        }
    }

    @Test
    public void smtpServerShouldReceiveEmailsOnConfiguredHostAndPort() {
        run(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.smtp-port=2526", "mailspy.smtp-bind-address=127.0.0.2")
                .withUserConfiguration(TestSmtpConfig.class),
            (context, mockMvc) -> {
                sendTestEmail(context);
                mockMvc.perform(get("/mailspy/mails/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].rawMessage", emailHeaderMatches("To", equalTo(TEST_RECIPIENT))));
            });
    }

    private void sendTestEmail(WebApplicationContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("");
        message.setTo(TEST_RECIPIENT);
        context.getBean(MailSender.class)
            .send(message);
    }

}
