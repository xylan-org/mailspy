package org.xylan.mailspy.core.config.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.support.csrf.SpringSecurityCsrfTokenRepository;

import java.util.List;

@Configuration
@ConditionalOnMailSpyEnabled
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@Import(MailSpyCsrfTokenRepositoryConfig.class)
public abstract class AbstractMailSpySecurityAutoConfiguration {

    @Autowired
    private MailSpyProperties properties;

    @Autowired
    @Qualifier("mailSpyCsrfTokenRepository")
    private SpringSecurityCsrfTokenRepository csrfTokenRepository;

    @SneakyThrows
    protected void configureHttpSecurity(HttpSecurity httpSecurity) {
        httpSecurity.regexMatcher(properties.getPathNoTrailingSlash() + "(/.*)?")
            .authorizeRequests()
            .anyRequest()
            .permitAll();
        if (properties.isEnableCors()) {
            httpSecurity.cors()
                .configurationSource((request) -> {
                    final CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
                    corsConfiguration.applyPermitDefaultValues();
                    return corsConfiguration;
                });
        }
        if (properties.isEnableCsrfProtection()) {
            httpSecurity.csrf()
                .csrfTokenRepository(csrfTokenRepository.getDelegateCsrfRepository());
        }
    }

}
