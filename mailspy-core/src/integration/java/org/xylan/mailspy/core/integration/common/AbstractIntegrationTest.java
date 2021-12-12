package org.xylan.mailspy.core.integration.common;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.xylan.mailspy.core.config.MailSpyAutoConfig;

public abstract class AbstractIntegrationTest {

    private WebApplicationContextRunner contextRunner;

    @SpringBootApplication
    protected static class TestUserConfig {
    }

    @BeforeClass
    protected final void initializeContextRunner() {
        contextRunner = new WebApplicationContextRunner()
            .withPropertyValues("mailspy.enabled=true")
            .withUserConfiguration(TestUserConfig.class)
            .withConfiguration(AutoConfigurations.of(MailSpyAutoConfig.class));
    }

    protected final void run(BiContextConsumer<WebApplicationContext, MockMvc> contextConsumer) {
        run(ContextRunnerCustomizer.identity(), contextConsumer);
    }

    protected final void run(ContextRunnerCustomizer contextCustomizer, BiContextConsumer<WebApplicationContext, MockMvc> contextConsumer) {
        contextCustomizer.customize(contextRunner)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                contextConsumer.accept(context, mockMvc);
            });
    }

}
