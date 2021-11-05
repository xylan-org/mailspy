package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnClass(SecurityFilterChain.class)
public @interface ConditionalOnWebSecurity {

}
