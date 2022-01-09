package org.xylan.mailspy.integration.common;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.xylan.mailspy.core.config.MailSpyBaseAutoConfig;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

public abstract class AbstractIntegrationTest {

    private WebApplicationContextRunner contextRunner;

    @SpringBootApplication
    @ComponentScan(excludeFilters = @ComponentScan.Filter(type= FilterType.REGEX, pattern=".*"))
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
        run(withoutSecurityAutoConfig(ContextRunnerCustomizer.identity()), contextConsumer);
    }

    protected final void run(
        ContextRunnerCustomizer contextCustomizer,
        BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer
    ) {
        run(withoutSecurityAutoConfig(contextCustomizer), contextConsumer, false);
    }

    private ContextRunnerCustomizer withoutSecurityAutoConfig(ContextRunnerCustomizer additionalCustomizer) {
        return (contextRunner -> additionalCustomizer.customize(
            contextRunner.withClassLoader(new FilteredClassLoader(SecurityFilterChain.class, HttpSecurity.class, EnableWebSecurity.class))));
    }

    protected final void runWithSecurity(BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer) {
        runWithSecurity(ContextRunnerCustomizer.identity(), contextConsumer);
    }

    protected final void runWithSecurity(
        ContextRunnerCustomizer contextCustomizer,
        BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer
    ) {
        run(contextCustomizer, contextConsumer, true);
    }

    private void run(
        ContextRunnerCustomizer contextCustomizer,
        BiContextConsumer<AssertableWebApplicationContext, MockMvc> contextConsumer,
        boolean withSecurity
    ) {
        contextCustomizer.customize(contextRunner)
            .run(context -> {
                AbstractMockMvcBuilder<?> mockMvcBuilder = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(sharedHttpSession());
                if (withSecurity) {
                    mockMvcBuilder = mockMvcBuilder.apply(springSecurity());
                }
                contextConsumer.accept(context, mockMvcBuilder.build());
            });
    }


}
