package org.xylan.mailspy.core.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnWebSecurity;

@ConditionalOnMailSpyEnabled
@ConditionalOnWebSecurity
@ConditionalOnBean({ WebSecurityConfigurer.class, HttpSecurity.class })
@ConditionalOnMissingBean(SecurityFilterChain.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class MailSpySecurityWithConfigurerAdapterAutoConfig extends AbstractMailSpySecurityAutoConfig {

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
        protected void configure(HttpSecurity httpSecurity) {
            configureHttpSecurity(httpSecurity);
        }
    }

}