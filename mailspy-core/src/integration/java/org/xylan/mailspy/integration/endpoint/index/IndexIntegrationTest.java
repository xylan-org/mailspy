package org.xylan.mailspy.integration.endpoint.index;

import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class IndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void indexShouldBeAvailableOnDefaultPathAndForwardToHtmlPageWhenNotOverridden() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/mailspy/resources/index.html"));
        });
    }

    @Test
    public void indexShouldBeAvailableOnCustomPathAndForwardToHtmlPageWhenOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/customMailSpyPath"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/customMailSpyPath"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("/customMailSpyPath/resources/index.html"));
            });
    }

    @Test
    public void indexShouldStripTrailingSlashWhenCustomPathContainsIt() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/customMailSpyPath/"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/customMailSpyPath"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("/customMailSpyPath/resources/index.html"));
            });
    }

    @Test
    public void indexHtmlPageShouldBeAvailableWithPrefixInBaseTag() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy/resources/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(xpath("//base/@href").string("/mailspy/resources/"));
        });
    }

    @Test
    public void indexHtmlPageShouldBeAvailableWithCustomPrefixInBaseTagWhenPrefixIsOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/custom"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/custom/resources/index.html"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andExpect(xpath("//base/@href").string("/custom/resources/"));
            });
    }

}
