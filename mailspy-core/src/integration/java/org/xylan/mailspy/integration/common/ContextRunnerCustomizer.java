package org.xylan.mailspy.integration.common;

import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

@FunctionalInterface
public interface ContextRunnerCustomizer {

    WebApplicationContextRunner customize(WebApplicationContextRunner contextRunner);

    static ContextRunnerCustomizer identity() {
        return contextRunner -> contextRunner;
    }

}