package org.xylan.mailspy.integration.endpoint.sse;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.web.subscription.sse.MailSpySseEmitterRegistry;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;
import org.xylan.mailspy.integration.common.matchers.sse.SseEventsPattern;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.emailTextMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.isValidUuid;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.sseEventsMatchPattern;

public class MailSubscriptionIntegrationTest extends BaseIntegrationTest {

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
