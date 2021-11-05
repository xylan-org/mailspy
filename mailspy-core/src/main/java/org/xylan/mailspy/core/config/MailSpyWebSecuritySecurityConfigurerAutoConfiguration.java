package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.support.csrf.SpringSecurityCsrfTokenRepository;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnMailSpyEnabled
@ConditionalOnBean(WebSecurityConfigurer.class)
@ConditionalOnMissingBean(SecurityFilterChain.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class MailSpyWebSecuritySecurityConfigurerAutoConfiguration {

    @Autowired
    private MailSpyProperties properties;

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
        public void init(WebSecurity web) {
            web.addSecurityFilterChainBuilder(() -> {
                RegexRequestMatcher requestMatcher = new RegexRequestMatcher(properties.getPathNoTrailingSlash() + "(/.*)?", null);
                List<Filter> filters = new ArrayList<>();
                if (properties.isEnableCors()) {
                    filters.add(createCorsFilter());
                }
                if (properties.isEnableCsrfProtection()) {
                    filters.add(new CsrfFilter(mailSpyCsrfTokenRepository().getDelegateCsrfRepository()));
                }
                return new DefaultSecurityFilterChain(requestMatcher, filters);
            });
        }

        private CorsFilter createCorsFilter() {
            final CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
            corsConfiguration.applyPermitDefaultValues();
            return new CorsFilter(request -> corsConfiguration);
        }
    }

}