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

package org.xylan.mailspy.integration.endpoint.index;

import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class IndexIntegrationTest extends BaseIntegrationTest {

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
