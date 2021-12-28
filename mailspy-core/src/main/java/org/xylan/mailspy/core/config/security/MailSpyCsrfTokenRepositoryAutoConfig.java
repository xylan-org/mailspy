package org.xylan.mailspy.core.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnMissingWebSecurity;
import org.xylan.mailspy.core.config.condition.ConditionalOnWebSecurity;
import org.xylan.mailspy.core.impl.web.support.csrf.NoOpCsrfTokenRepository;
import org.xylan.mailspy.core.impl.web.support.csrf.SpringSecurityCsrfTokenRepository;

@Configuration
@ConditionalOnMailSpyEnabled
public class MailSpyCsrfTokenRepositoryAutoConfig {

    @Bean
    @ConditionalOnWebSecurity
    @Qualifier("mailSpyCsrfTokenRepository")
    public SpringSecurityCsrfTokenRepository mailSpyCsrfTokenRepository() {
        SpringSecurityCsrfTokenRepository csrfTokenRepository = new SpringSecurityCsrfTokenRepository();
        csrfTokenRepository.setDelegateCsrfRepository(new HttpSessionCsrfTokenRepository());
        return csrfTokenRepository;
    }

    @Bean
    @ConditionalOnMissingWebSecurity
    @Qualifier("mailSpyCsrfTokenRepository")
    public NoOpCsrfTokenRepository mailSpyNoOpCsrfTokenRepository() {
        return new NoOpCsrfTokenRepository();
    }

}
