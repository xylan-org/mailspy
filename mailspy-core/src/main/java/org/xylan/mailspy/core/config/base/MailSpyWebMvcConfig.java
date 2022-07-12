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

package org.xylan.mailspy.core.config.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.support.MailSpyIndexPageResourceTransformer;

/**
 * Configuration for MailSpy's web layer.
 */
@Configuration
public class MailSpyWebMvcConfig {

    @Bean
    public WebMvcConfigurer mailSpyWebMvcConfigurer() {
        return new MailSpyWebMvcConfigurer();
    }

    /**
     * MailSpy's {@link WebMvcConfigurer}.
     */
    public static class MailSpyWebMvcConfigurer implements WebMvcConfigurer {

        @Autowired
        private MailSpyIndexPageResourceTransformer indexPageResourceTransformer;

        @Autowired
        private MailSpyProperties properties;

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.addResourceHandler(properties.getPathNoTrailingSlash() + "/resources/**")
                    .addResourceLocations("classpath:META-INF/mailspy-frontend/")
                    .resourceChain(true)
                    .addTransformer(indexPageResourceTransformer);
        }

        @Override
        public void configurePathMatch(final PathMatchConfigurer configurer) {
            configurer.addPathPrefix(
                    properties.getPathNoTrailingSlash(), HandlerTypePredicate.forBasePackage("org.xylan.mailspy.core"));
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            if (properties.isEnableCors()) {
                registry.addMapping("/**").allowedOriginPatterns("*");
            }
        }
    }
}
