package org.xylan.mailspy.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnMissingSpringWebSecurity;
import org.xylan.mailspy.core.web.support.csrf.NoOpCsrfTokenRepository;

@Configuration
@ConditionalOnMailSpyEnabled
@ConditionalOnMissingSpringWebSecurity
public class MailSpyWebSecurityNoOpAutoConfiguration {

    @Bean
    public NoOpCsrfTokenRepository mailSpyCsrfTokenRepository() {
        return new NoOpCsrfTokenRepository();
    }

}
