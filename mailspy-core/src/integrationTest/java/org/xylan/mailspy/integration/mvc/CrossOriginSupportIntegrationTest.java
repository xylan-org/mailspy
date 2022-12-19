/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.integration.mvc;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

public class CrossOriginSupportIntegrationTest extends BaseIntegrationTest {

    @Test
    public void corsSupportShouldAddHeaderToResponseWhenEnabled() {
        run((contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=true"), (context, mockMvc) -> {
            mockMvc.perform(get("/mailspy/ws")
                            .header(ACCESS_CONTROL_REQUEST_METHOD, "GET")
                            .header(ORIGIN, "http://www.example.com"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.example.com"));
        });
    }

    @Test
    public void corsSupportShouldNotAddHeaderToResponseAndRespondForbiddenWhenDisabled() {
        run((contextRunner) -> contextRunner.withPropertyValues("mailspy.enable-cors=false"), (context, mockMvc) -> {
            mockMvc.perform(get("/mailspy/ws")
                            .header(ACCESS_CONTROL_REQUEST_METHOD, "GET")
                            .header(ORIGIN, "http://www.example.com"))
                    .andExpect(status().isForbidden())
                    .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
        });
    }
}
