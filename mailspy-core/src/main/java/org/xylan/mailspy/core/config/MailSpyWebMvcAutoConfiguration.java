package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.web.support.MailSpyIndexPageResourceTransformer;

@ConditionalOnMailSpyEnabled
public class MailSpyWebMvcAutoConfiguration {

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

    }
}
