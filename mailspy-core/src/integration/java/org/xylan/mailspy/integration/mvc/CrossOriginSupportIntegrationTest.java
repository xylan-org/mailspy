package org.xylan.mailspy.integration.mvc;

import org.springframework.http.HttpHeaders;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CrossOriginSupportIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void corsSupportShouldAddHeaderToResponseWhenEnabledAndAppHasSecurity() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=true"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.example.com"));
        });
    }

    @Test
    public void corsSupportShouldNotAddHeaderToResponseWhenDisabled() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=false"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
            });
    }

}
