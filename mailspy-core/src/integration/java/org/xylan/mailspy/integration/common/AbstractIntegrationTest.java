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

public abstract class AbstractIntegrationTest {

    private WebApplicationContextRunner contextRunner;

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
