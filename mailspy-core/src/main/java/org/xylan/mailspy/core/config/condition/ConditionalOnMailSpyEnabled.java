package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import java.lang.annotation.*;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "mailspy.enabled", havingValue = "true")
@ConditionalOnWebApplication(type = SERVLET)
public @interface ConditionalOnMailSpyEnabled {
}
