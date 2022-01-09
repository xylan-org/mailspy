package org.xylan.mailspy.core.integration.security;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityFilterChainSupportIntegrationTest extends AbstractIntegrationTest {

    @TestConfiguration
    public static class TestWebSecurityConfig {
        @Bean
        @SneakyThrows
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) {
            // deny all to see if mailspy can let itself through
            return http.authorizeRequests()
                .anyRequest()
                .denyAll()
                .and()
                .build();
        }
    }

    @Test
    public void mailSpyShouldBeAvailableWhenHostAppUsesSecurityFilterChainDirectlyAndOrderIsDefault() {
        runWithSecurity(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebSecurityConfig.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isOk());
            });
    }

    /*
     * Security filter chain itself does not support ordering, and ordering the bean definition has no effect here.
     * Since only MailSpy's filter chain has an order value, it will always be injected first into the filter chain proxy.
     * Thus, it's expected that the property doesn't do anything in this case.
     */
    @Test
    public void mailSpyShouldBeAvailableWhenHostAppUsesSecurityFilterChainDirectlyAndOrderIsSetHigherThenUserConfig() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withUserConfiguration(TestWebSecurityConfig.class)
                .withPropertyValues("mailspy.security-order=101"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void mailSpyShouldBeAvailableWhenHostAppUsesSecurityAutoConfigAndOrderIsDefault() {
        runWithSecurity((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy"))
                .andExpect(status().isOk());
        });
    }

    // Same as with direct security chain definition: ordering should put MailSpy first, regardless of the value.
    @Test
    public void mailSpyShouldBeAvailableWhenHostAppUsesSecurityAutoConfigAndOrderIsSetHigherThenUserConfig() {
        runWithSecurity(
            (contextRunner) -> contextRunner
                .withPropertyValues("mailspy.security-order=101"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isOk());
            });
    }


}
