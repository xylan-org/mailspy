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

@Configuration
public class MailSpyWebMvcConfig {

    @Bean
    public WebMvcConfigurer mailSpyWebMvcConfigurer() {
        return new MailSpyWebMvcConfigurer();
    }

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
            configurer.addPathPrefix(properties.getPathNoTrailingSlash(),
                HandlerTypePredicate.forBasePackage("org.xylan.mailspy.core"));
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            if (properties.isEnableCors()) {
                registry.addMapping("/**").allowedOriginPatterns("*");
            }
        }

    }
}
