package org.xylan.mailspy.integration.smtp;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailHeaderMatches;

public class SmtpServerIntegrationTest extends AbstractIntegrationTest {

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
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].rawMessage", emailHeaderMatches("To", equalTo(TEST_RECIPIENT))));
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
