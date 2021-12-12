package org.xylan.mailspy.core.integration.history;

import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.integration.common.AbstractIntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.xylan.mailspy.core.integration.common.matchers.MailSpyMatchers.*;

public class ClearHistoryIntegrationTest extends AbstractIntegrationTest {

    public static final String HISTORY_ENDPOINT = "/mailspy/mails/history";

    @Test
    public void deleteShouldClearHistory() {
        run((context, mockMvc) -> {
            // GIVEN
            mockMvc.perform(get(HISTORY_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()))
                .andExpect(status().isOk());
            sendTestMail(context);
            mockMvc.perform(get(HISTORY_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());

            // WHEN
            mockMvc.perform(delete(HISTORY_ENDPOINT))
                .andExpect(status().isOk());

            // THEN
            mockMvc.perform(get(HISTORY_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()))
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
