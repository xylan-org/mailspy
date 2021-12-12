package org.xylan.mailspy.core.integration.index;

import org.testng.annotations.Test;
import org.xylan.mailspy.core.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void indexShouldBeAvailableOnDefaultPathWhenNotOverridden() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy"))
                .andExpect(forwardedUrl("/mailspy/resources/index.html"))
                .andExpect(status().isOk());
        });
    }

    @Test
    public void indexShouldBeAvailableOnCustomPathWhenOverridden() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/customMailSpyPath"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/customMailSpyPath"))
                    .andExpect(forwardedUrl("/customMailSpyPath/resources/index.html"))
                    .andExpect(status().isOk());
        });
    }

    @Test
    public void indexShouldStripTrailingSlashWhenCustomPathContainsIt() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.path=/customMailSpyPath/"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/customMailSpyPath"))
                    .andExpect(forwardedUrl("/customMailSpyPath/resources/index.html"))
                    .andExpect(status().isOk());
        });
    }

}
