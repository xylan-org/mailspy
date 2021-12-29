package org.xylan.mailspy.core.integration.sse;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.web.subscription.sse.MailSpySseEmitterRegistry;
import org.xylan.mailspy.core.integration.common.AbstractIntegrationTest;
import org.xylan.mailspy.core.integration.common.matchers.sse.SseEventsPattern;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.xylan.mailspy.core.integration.common.matchers.MailSpyMatchers.*;

public class MailSubscriptionIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_RECIPIENT = "test@example.com";
    private static final String TEST_MESSAGE = "Test Message";

    @Test
    public void subscribeEndpointShouldEmitCorrectEmailForSubscribers() {
        run((context, mockMvc) -> {
            // GIVEN
            MvcResult mvcResult = mockMvc.perform(get("/mailspy/mails/subscribe"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

            // WHEN
            sendTestEmail(context);
            closeSseEmitters(context);

            // THEN
            mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(content().string(sseEventsMatchPattern(
                    SseEventsPattern.create()
                        .name(equalTo("connected"))
                        .data(equalTo("connected"))
                        .emptyLine()
                        .name(equalTo("mail"))
                        .data(allOf(
                            jsonPathMatches("$.id", isValidUuid()),
                            jsonPathMatches("$.exception", nullValue()),
                            jsonPathMatches("$.rawMessage", emailHeaderMatches("To", equalTo(TEST_RECIPIENT))),
                            jsonPathMatches("$.rawMessage", emailTextMatches(equalTo(TEST_MESSAGE)))
                        ))
                        .emptyLine()
                        .emptyLine()
                )));
        });
    }

    @Test
    public void subscribeEndpointShouldBeAvailableOnCustomPathWhenOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/custom"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/custom/mails/subscribe"))
                    .andExpect(status().isOk())
                    .andExpect(request().asyncStarted())
                    .andReturn();
            });
    }

    // it looks like MockMvc sees the emitted data only when emitters complete
    private void closeSseEmitters(WebApplicationContext context) {
        context.getBean(MailSpySseEmitterRegistry.class)
            .completeEmitters();
    }

    private void sendTestEmail(WebApplicationContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(TEST_MESSAGE);
        message.setTo(TEST_RECIPIENT);
        context.getBean(MailSender.class)
                .send(message);
    }

}
