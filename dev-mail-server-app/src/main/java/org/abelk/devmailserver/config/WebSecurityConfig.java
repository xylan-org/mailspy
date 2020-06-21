package org.abelk.devmailserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @ConfigurationProperties("devmailserver.app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        if (appProperties().isCors()) {
            http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        }
    }

}
