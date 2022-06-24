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

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.testng.annotations.Test;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WebMvcConfigurerSupportIntegrationTest extends BaseIntegrationTest {

    public static class TestWebMvcConfigurer implements WebMvcConfigurer {

        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:META-INF/test-resources/")
                .resourceChain(true);
        }

        @Override
        public void configurePathMatch(final PathMatchConfigurer configurer) {
            configurer.addPathPrefix("/test",
                HandlerTypePredicate.forBasePackage("org.xylan.mailspy.integration"));
        }
    }

    @RestController
    public static class TestController {
        @RequestMapping("/test-method")
        @ResponseStatus(HttpStatus.OK)
        public void testMethod() {
            // method body does not matter
        }
    }

    @Test
    public void endpointsDefinedByHostAppShouldStillBeAvailableWithMailSpyEnabled() {
        run(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebMvcConfigurer.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/test/test-method"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void resourcesDefinedByHostAppShouldStillBeAvailableWithMailSpyEnabled() {
        run(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebMvcConfigurer.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/resources/test.txt"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void mailSpyShouldStillBeAvailableWhenHostAppDefinedMvcConfigurer() {
        run(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebMvcConfigurer.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    public void mailSpyResourcesShouldStillBeAvailableWhenHostAppDefinedMvcConfigurer() {
        run(
            (contextRunner) -> contextRunner.withUserConfiguration(TestWebMvcConfigurer.class),
            (context, mockMvc) -> {
                mockMvc.perform(get("/mailspy/resources/index.html"))
                    .andExpect(status().isOk());
            });
    }

}
