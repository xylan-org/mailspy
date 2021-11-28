package org.xylan.mailspy.core.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnMissingWebSecurity;
import org.xylan.mailspy.core.impl.web.support.csrf.NoOpCsrfTokenRepository;

@Configuration
@ConditionalOnMailSpyEnabled
@ConditionalOnMissingWebSecurity
public class MailSpySecurityNoOpAutoConfiguration {

    @Bean
    public NoOpCsrfTokenRepository mailSpyCsrfTokenRepository() {
        return new NoOpCsrfTokenRepository();
    }

}
