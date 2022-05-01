package org.xylan.mailspy.integration.common;

import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

/**
 * Functional interface of lambdas that customize the web application context runner in some integration test cases.
 */
@FunctionalInterface
public interface ContextRunnerCustomizer {

    /**
     * Customizes the web application context runner.
     * @param contextRunner The web application context runner.
     * @return The customized runner.
     */
    WebApplicationContextRunner customize(WebApplicationContextRunner contextRunner);

    /**
     * Identity function for the interface. Represents no customization done.
     * @return The identity customizer function.
     */
    static ContextRunnerCustomizer identity() {
        return contextRunner -> contextRunner;
    }

}
