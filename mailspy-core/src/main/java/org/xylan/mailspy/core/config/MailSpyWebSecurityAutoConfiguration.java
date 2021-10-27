package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnWebSecurity;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.support.csrf.SpringSecurityCsrfTokenRepository;

import java.util.List;

@Configuration
@ConditionalOnMailSpyEnabled
@ConditionalOnWebSecurity
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class MailSpyWebSecurityAutoConfiguration {

    @Bean
    public SpringSecurityCsrfTokenRepository mailSpyCsrfTokenRepository() {
        SpringSecurityCsrfTokenRepository csrfTokenRepository = new SpringSecurityCsrfTokenRepository();
        csrfTokenRepository.setDelegateCsrfRepository(new HttpSessionCsrfTokenRepository());
        return csrfTokenRepository;
    }

    @Bean
    public MailSpyWebSecurityConfigurer mailSpyWebSecurityConfigurer() {
        return new MailSpyWebSecurityConfigurer();
    }

    public class MailSpyWebSecurityConfigurer extends WebSecurityConfigurerAdapter implements Ordered {

        @Autowired
        private MailSpyProperties properties;

        @Override
        public int getOrder() {
            return properties.getSecurityOrder();
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.regexMatcher(properties.getPathNoTrailingSlash() + "(/.*)?");
            if (properties.isEnableCors()) {
                final CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
                corsConfiguration.applyPermitDefaultValues();
                http.cors().configurationSource(request -> corsConfiguration);
            }
            if (properties.isEnableCsrfProtection()) {
                http.csrf().csrfTokenRepository(mailSpyCsrfTokenRepository().getDelegateCsrfRepository());
            } else {
                http.csrf().disable();
            }
        }
    }
}