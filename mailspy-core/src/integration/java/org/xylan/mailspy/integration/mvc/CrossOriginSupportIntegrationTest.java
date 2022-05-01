package org.xylan.mailspy.integration.mvc;

import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CrossOriginSupportIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void corsSupportShouldAddHeaderToResponseWhenEnabledAndAppHasSecurity() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=true"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.example.com"));
            });
    }

    @Test
    public void corsSupportShouldNotAddHeaderToResponseWhenDisabled() {
        run(
            (contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=false"),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/mails/history")
                        .header(ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
            });
    }

}
