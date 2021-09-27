package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class PresentSpringWebSecurityCondition extends AnyNestedCondition {

    public PresentSpringWebSecurityCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnClass({ SecurityFilterChain.class })
    static class OnMissingFilterChain {
    }

    @ConditionalOnClass({ HttpSecurity.class })
    static class OnMissingHttpSecurity {
    }

}
