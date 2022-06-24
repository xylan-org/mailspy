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

package org.xylan.mailspy.integration.mvc;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.subethamail.smtp.server.SMTPServer;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.MailSpyBaseAutoConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebMvcConfig.MailSpyWebMvcConfigurer;
import org.xylan.mailspy.integration.common.BaseIntegrationTest.TestUserConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class MissingWebMvcIntegrationTest {

    @Test
    public void mailSpyBeansShouldNotBeCreatedWhenEnabledInNonServletApp() {
        new ApplicationContextRunner()
            .withPropertyValues("mailspy.enabled=true")
            .withUserConfiguration(TestUserConfig.class)
            .withConfiguration(AutoConfigurations.of(MailSpyBaseAutoConfig.class))
            .run((context) -> {
                assertThat(context).doesNotHaveBean(MailSpyWebMvcConfigurer.class);
                assertThat(context).doesNotHaveBean(SMTPServer.class);
            });
    }

    @Test
    public void mailSpyBeansShouldNotBeCreatedWhenDisabledInServletApp() {
        new WebApplicationContextRunner()
            .withPropertyValues("mailspy.enabled=false")
            .withUserConfiguration(TestUserConfig.class)
            .withConfiguration(AutoConfigurations.of(MailSpyBaseAutoConfig.class))
            .run((context) -> {
                assertThat(context).doesNotHaveBean(MailSpyWebMvcConfigurer.class);
                assertThat(context).doesNotHaveBean(SMTPServer.class);
            });
    }

}
