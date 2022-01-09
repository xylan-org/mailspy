package org.xylan.mailspy.integration.security;

import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MissingSecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void mailSpyShouldBeAvailableAndSecurityBeansShouldBeMissingWhenHostAppHasNoSecurity() {
        run((context, mockMvc) -> {
            mockMvc.perform(get("/mailspy"))
                .andExpect(status().isOk());
            assertThat(context).doesNotHaveBean(SecurityFilterChain.class);
            assertThat(context).doesNotHaveBean(WebSecurityConfigurer.class);
            assertThat(context).doesNotHaveBean(HttpSecurity.class);
        });
    }

}
