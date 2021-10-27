package org.xylan.mailspy.core.config.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(MissingSpringWebSecurityCondition.class)
public @interface ConditionalOnMissingWebSecurity {

}
