package org.xylan.mailspy.integration.mvc;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.subethamail.smtp.server.SMTPServer;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.MailSpyBaseAutoConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebMvcConfig.MailSpyWebMvcConfigurer;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest.TestUserConfig;

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
                assertThat(context).doesNotHaveBean("mailSpySecurityFilterChain");
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
                assertThat(context).doesNotHaveBean("mailSpySecurityFilterChain");
            });
    }

}