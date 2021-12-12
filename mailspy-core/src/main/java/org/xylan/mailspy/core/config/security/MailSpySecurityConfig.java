package org.xylan.mailspy.core.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    MailSpySecurityWithConfigurerAdapterConfig.class,
    MailSpySecurityWithFilterChainConfig.class,
    MailSpyCsrfTokenRepositoryConfig.class
})
public class MailSpySecurityConfig {
}
