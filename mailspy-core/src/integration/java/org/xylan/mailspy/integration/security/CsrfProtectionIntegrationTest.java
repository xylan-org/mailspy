package org.xylan.mailspy.integration.security;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;
import org.xylan.mailspy.integration.common.xml.XPathUtils;
import org.xylan.mailspy.integration.common.matchers.MailSpyMatchers;

import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CsrfProtectionIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void csrfTokenShouldBeInIndexPageMetaTagWhenAppHasSecurityAndCsrfProtectionEnabled() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=true"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/resources/index.html"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andExpect(xpath("//meta[@name='csrf_token']/@content").string(MailSpyMatchers.isValidUuid()));
            });
    }

    @Test
    public void csrfTokenShouldNotBeInIndexPageMetaTagWhenAppHasNoSecurity() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy/resources/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(xpath("//meta[@name='csrf_token']/@content").string(emptyString()));
        });
    }

    @Test
    public void csrfTokenShouldNotBeInIndexPageMetaTagWhenCsrfProtectionDisabled() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=false"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/resources/index.html"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andExpect(xpath("//meta[@name='csrf_token']/@content").string(emptyString()));
            });
    }

    @Test
    public void csrfProtectionShouldAllowStateMutatingRequestWhenItIsEnabledAndAppHasSecurityAndCorrectCsrfTokenReceived() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=true"),
            (context, mockMvc) -> {
                String csrfToken = getCsrfToken(mockMvc);
                mockMvc.perform(delete("/mailspy/mails/history")
                    .header("X-CSRF-TOKEN", csrfToken))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void csrfProtectionShouldForbidStateMutatingRequestWhenItIsEnabledAndAppHasSecurityAndIncorrectCsrfTokenReceived() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=true"),
            (context, mockMvc) -> {
                getCsrfToken(mockMvc);
                mockMvc.perform(delete("/mailspy/mails/history")
                    .header("X-CSRF-TOKEN", "I like turtles"))
                    .andExpect(status().isForbidden());
            });
    }

    @Test
    public void csrfProtectionShouldAllowStateMutatingRequestWhenItIsDisabled() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=false"),
            (context, mockMvc) -> {
                mockMvc.perform(delete("/mailspy/mails/history"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void csrfProtectionShouldAllowStateMutatingRequestWhenAppHasNoSecurity() {
        run(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.enable-csrf-protection=true"),
            (context, mockMvc) -> {
                mockMvc.perform(delete("/mailspy/mails/history"))
                    .andExpect(status().isOk());
            });
    }

    @SneakyThrows
    private String getCsrfToken(MockMvc mockMvc) {
        String indexPageAsString = mockMvc.perform(get("/mailspy/resources/index.html"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return XPathUtils.getValueByXPath(indexPageAsString,"//meta[@name='csrf_token']/@content");
    }

}
