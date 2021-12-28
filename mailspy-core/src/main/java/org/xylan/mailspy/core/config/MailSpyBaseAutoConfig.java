package org.xylan.mailspy.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.base.MailSpySmtpServerConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebMvcConfig;

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
