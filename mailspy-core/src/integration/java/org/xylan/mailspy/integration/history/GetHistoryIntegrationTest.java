package org.xylan.mailspy.integration.history;

import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;
import org.xylan.mailspy.integration.common.matchers.MailSpyMatchers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetHistoryIntegrationTest extends AbstractIntegrationTest {

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", MailSpyMatchers.isValidUuid()))
                .andExpect(jsonPath("$[0].exception", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rawMessage", MailSpyMatchers.emailHeaderMatches("To", equalTo(TEST_RECIPIENT))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rawMessage", MailSpyMatchers.emailTextMatches(equalTo(TEST_MESSAGE))));
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
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].rawMessage", MailSpyMatchers.emailTextMatches(equalTo(testMessage2))));
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
