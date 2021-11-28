package org.xylan.mailspy.app;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class TestFilterChainConfig {

    //@Bean
    @SneakyThrows
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.authorizeRequests().anyRequest().denyAll();
        return httpSecurity.build();
    }

}