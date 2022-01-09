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
import org.xylan.mailspy.integration.common.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WebMvcConfigurerSupportIntegrationTest extends AbstractIntegrationTest {

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
        public void testMethod() {}
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
