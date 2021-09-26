package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;

public class MissingSpringWebSecurityCondition extends AnyNestedCondition {

    public MissingSpringWebSecurityCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnMissingClass("org.springframework.security.web.SecurityFilterChain")
    static class OnMissingFilterChain {
    }

    @ConditionalOnMissingClass("org.springframework.security.config.annotation.web.builders.HttpSecurity")
    static class OnMissingHttpSecurity {
    }

}
