package org.xylan.mailspy.core.config.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;

import java.lang.annotation.*;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "mailspy.enabled", havingValue = "true")
@ConditionalOnWebApplication(type = SERVLET)
public @interface ConditionalOnMailSpyEnabled {
}
