package org.xylan.mailspy.core.integration.security;

import org.springframework.http.HttpHeaders;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CrossOriginSupportIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void corsSupportShouldAddHeaderToResponseWhenEnabledAndAppHasSecurity() {
        runWithSecurity(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=true"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        });
    }

    @Test
    public void corsSupportShouldNotAddHeaderToResponseWhenDisabled() {
        runWithSecurity(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=false"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
            });
    }

    @Test
    public void corsSupportShouldNotAddHeaderToResponseWhenAppHasNoSecurity() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=true"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
            });
    }

}