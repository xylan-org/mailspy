package org.xylan.mailspy.integration.security;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WebSecurityConfigurerSupportIntegrationTest extends AbstractIntegrationTest {

    public static class TestWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
        @Override
        @SneakyThrows
        protected void configure(HttpSecurity httpSecurity) {
            // deny all to see if mailspy can let itself through
            httpSecurity.authorizeRequests()
                .anyRequest()
                .denyAll();
        }
    }

    @Test
    public void mailSpyShouldBeAvailableWhenHostAppUsesWebSecurityConfigurerAndOrderIsDefault() {
        runWithSecurity(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebSecurityConfigurer.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void mailSpyShouldNotBeAvailableWhenHostAppUsesWebSecurityConfigurerAndOrderIsSetHigherThenUserConfig() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withUserConfiguration(TestWebSecurityConfigurer.class)
                .withPropertyValues("mailspy.security-order=101"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isForbidden());
            });
    }

}
