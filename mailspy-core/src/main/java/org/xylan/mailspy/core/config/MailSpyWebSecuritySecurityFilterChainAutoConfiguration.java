package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.security.OrderableSecurityFilterChainDecorator;
import org.xylan.mailspy.core.impl.web.support.csrf.SpringSecurityCsrfTokenRepository;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnMailSpyEnabled
@ConditionalOnBean(SecurityFilterChain.class)
@ConditionalOnMissingBean(WebSecurityConfigurer.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class MailSpyWebSecuritySecurityFilterChainAutoConfiguration {

    @Autowired
    private MailSpyProperties properties;

    @Bean
    public SpringSecurityCsrfTokenRepository mailSpyCsrfTokenRepository() {
        SpringSecurityCsrfTokenRepository csrfTokenRepository = new SpringSecurityCsrfTokenRepository();
        csrfTokenRepository.setDelegateCsrfRepository(new HttpSessionCsrfTokenRepository());
        return csrfTokenRepository;
    }

    @Bean
    public SecurityFilterChain mailSpySecurityFilterChain() {
        RegexRequestMatcher requestMatcher = new RegexRequestMatcher(properties.getPathNoTrailingSlash() + "(/.*)?", null);
        List<Filter> filters = new ArrayList<>();
        if (properties.isEnableCors()) {
            filters.add(createCorsFilter());
        }
        if (properties.isEnableCsrfProtection()) {
            filters.add(new CsrfFilter(mailSpyCsrfTokenRepository().getDelegateCsrfRepository()));
        }
        return new OrderableSecurityFilterChainDecorator(new DefaultSecurityFilterChain(requestMatcher, filters), properties.getSecurityOrder());
    }

    private CorsFilter createCorsFilter() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
        corsConfiguration.applyPermitDefaultValues();
        return new CorsFilter(request -> corsConfiguration);
    }

}