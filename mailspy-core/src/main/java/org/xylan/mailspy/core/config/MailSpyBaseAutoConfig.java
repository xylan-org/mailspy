package org.xylan.mailspy.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.xylan.mailspy.core.config.base.MailSpySmtpServerConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebMvcConfig;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;

/**
 * Base autoconfiguration to hold common conditions and import further configuration classes.s
 */
@Configuration
@ConditionalOnMailSpyEnabled
@EnableConfigurationProperties(MailSpyProperties.class)
@ComponentScan("org.xylan.mailspy.core.impl")
@Import({
    MailSpyWebMvcConfig.class,
    MailSpySmtpServerConfig.class
})
public class MailSpyBaseAutoConfig {
}
