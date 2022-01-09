package org.xylan.mailspy.core.config.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.condition.ConditionalOnWebSecurity;
import org.xylan.mailspy.core.impl.security.OrderableSecurityFilterChainDecorator;

@ConditionalOnMailSpyEnabled
@ConditionalOnWebSecurity
@ConditionalOnBean({ SecurityFilterChain.class, HttpSecurity.class })
@ConditionalOnMissingBean(WebSecurityConfigurer.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class MailSpySecurityWithFilterChainAutoConfig extends AbstractMailSpySecurityAutoConfig {

    @Autowired
    private MailSpyProperties properties;

    @Bean
    @SneakyThrows
    public SecurityFilterChain mailSpySecurityFilterChain(HttpSecurity httpSecurity) {
        configureHttpSecurity(httpSecurity);
        return new OrderableSecurityFilterChainDecorator(httpSecurity.build(), properties.getSecurityOrder());
    }

}