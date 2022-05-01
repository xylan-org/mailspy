package org.xylan.mailspy.integration.endpoint.history;

import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClearHistoryIntegrationTest extends AbstractIntegrationTest {

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
