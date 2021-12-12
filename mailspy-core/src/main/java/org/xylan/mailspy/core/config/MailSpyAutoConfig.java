package org.xylan.mailspy.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.etc.MailSpyMailSenderConfig;
import org.xylan.mailspy.core.config.etc.MailSpySmtpServerConfig;
import org.xylan.mailspy.core.config.etc.MailSpyWebMvcConfig;
import org.xylan.mailspy.core.config.security.MailSpySecurityConfig;

@Configuration
@ConditionalOnMailSpyEnabled
@EnableConfigurationProperties(MailSpyProperties.class)
@ComponentScan("org.xylan.mailspy.core.impl")
@Import({
    MailSpyWebMvcConfig.class,
    MailSpySecurityConfig.class,
    MailSpySmtpServerConfig.class,
    MailSpyMailSenderConfig.class
})
public class MailSpyAutoConfig {
}
