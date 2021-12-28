package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnMissingWebSecurity.MissingWebSecurityCondition.class)
public @interface ConditionalOnMissingWebSecurity {

    class MissingWebSecurityCondition extends AnyNestedCondition {
        MissingWebSecurityCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnMissingClass("org.springframework.security.web.SecurityFilterChain")
        static class SecurityFilterChainMissing { }

        @ConditionalOnMissingClass("org.springframework.security.config.annotation.web.builders.HttpSecurity")
        static class HttpSecurityMissing { }
    }

}
