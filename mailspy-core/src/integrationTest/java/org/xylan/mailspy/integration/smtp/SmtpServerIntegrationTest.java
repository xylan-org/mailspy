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
import org.springframework.messaging.Message;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailTextMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messagePayloadMatches;

public class SmtpServerIntegrationTest extends BaseIntegrationTest {

    private static final String RECIPIENT = "recipient@example.com";
    private static final String SENDER = "sender@example.com";
    private static final String SUBJECT = "test subject";
    private static final String TEXT = "test email text";

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
        runWithWs(
                (contextRunner) -> contextRunner
                        .withPropertyValues("mailspy.smtp-port=2526", "mailspy.smtp-bind-address=127.0.0.2")
                        .withUserConfiguration(TestSmtpConfig.class),
                (context, ws) -> {
                    // GIVEN
                    sendTestEmail(context);

                    // WHEN
                    Message<?> message = ws.awaitMessageSent();

                    // THEN
                    assertThat(message, messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/email")));
                    assertThat(message, messagePayloadMatches(jsonPathMatches("$.rawMessage", allOf(
                        emailTextMatches(equalTo(TEXT)),
                        emailHeaderMatches("To", equalTo(RECIPIENT)),
                        emailHeaderMatches("From", equalTo(SENDER)),
                        emailHeaderMatches("Subject", equalTo(SUBJECT))))));
                });
    }

    private void sendTestEmail(WebApplicationContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(TEXT);
        message.setTo(RECIPIENT);
        message.setFrom(SENDER);
        message.setSubject(SUBJECT);
        context.getBean(MailSender.class).send(message);
    }
}
