package org.xylan.mailspy.core.config.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.security.OrderableSecurityFilterChainDecorator;

@ConditionalOnBean(SecurityFilterChain.class)
@ConditionalOnMissingBean(WebSecurityConfigurer.class)
public class MailSpySecurityWithFilterChainAutoConfiguration extends AbstractMailSpySecurityAutoConfiguration {

    @Autowired
    private MailSpyProperties properties;

    @Bean
    @SneakyThrows
    public SecurityFilterChain mailSpySecurityFilterChain(HttpSecurity httpSecurity) {
        configureHttpSecurity(httpSecurity);
        return new OrderableSecurityFilterChainDecorator(httpSecurity.build(), properties.getSecurityOrder());
    }

}