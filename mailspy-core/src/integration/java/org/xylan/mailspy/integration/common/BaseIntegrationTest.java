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

package org.xylan.mailspy.integration.common;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.xylan.mailspy.core.config.MailSpyBaseAutoConfig;

import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

public class BaseIntegrationTest {

    private WebApplicationContextRunner contextRunner;

    protected BaseIntegrationTest() {
        // to prevent direct instantiation
    }

    @SpringBootApplication
    @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*"))
    public static class TestUserConfig {
    }

    @BeforeMethod
    protected final void initializeContextRunner() {
        contextRunner = new WebApplicationContextRunner()
            .withPropertyValues("mailspy.enabled=true")
            .withUserConfiguration(TestUserConfig.class)
            .withConfiguration(AutoConfigurations.of(MailSpyBaseAutoConfig.class));
    }

    protected final void run(BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer) {
        run(ContextRunnerCustomizer.identity(), contextConsumer);
    }

    protected final void run(
        ContextRunnerCustomizer contextCustomizer,
        BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer
    ) {
        contextCustomizer.customize(contextRunner)
            .run(context -> {
                AbstractMockMvcBuilder<?> mockMvcBuilder = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(sharedHttpSession());
                contextConsumer.accept(context, mockMvcBuilder.build());
            });
    }


}
