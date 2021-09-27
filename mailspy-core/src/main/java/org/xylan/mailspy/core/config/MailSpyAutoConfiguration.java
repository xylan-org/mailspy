package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;
import org.xylan.mailspy.core.config.condition.ConditionalOnMissingSpringWebSecurity;
import org.xylan.mailspy.core.config.condition.ConditionalOnSpringWebSecurity;
import org.xylan.mailspy.core.subetha.EventPublishingMessageHandler;
import org.xylan.mailspy.core.web.support.MailSpyIndexPageResourceTransformer;
import org.xylan.mailspy.core.web.support.csrf.NoOpCsrfTokenRepository;
import org.xylan.mailspy.core.web.support.csrf.SpringSecurityCsrfTokenRepository;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@Configuration
@ConditionalOnProperty(name = "mailspy.enabled", havingValue = "true")
@ConditionalOnWebApplication(type = SERVLET)
@EnableConfigurationProperties(MailSpyProperties.class)
@ComponentScan("org.xylan.mailspy.core")
public class MailSpyAutoConfiguration {

    @Autowired
    private MailSpyProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer mailSpySmtpServer() {
        final SMTPServer smtpServer = new SMTPServer(context -> mailSpyMessageHandler());
        smtpServer.setPort(properties.getSmtpPort());
        smtpServer.setBindAddress(properties.getBindAddress());
        return smtpServer;
    }

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public MessageHandler mailSpyMessageHandler() {
        return new EventPublishingMessageHandler();
    }

    @Configuration
    @AutoConfigureAfter(MailSenderAutoConfiguration.class)
    @ConditionalOnProperty(name = "mailspy.sender.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(JavaMailSender.class)
    public static class MailSpySenderAutoConfiguration {

        @Autowired
        private MailSpyProperties properties;

        @Bean
        public JavaMailSenderImpl mailSender() {
            final JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(properties.getBindAddress().getHostAddress());
            sender.setPort(properties.getSmtpPort());
            return sender;
        }

    }

    @Configuration
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

    @Configuration
    @ConditionalOnSpringWebSecurity
    @AutoConfigureAfter(SecurityAutoConfiguration.class)
    public static class MailSpyWebSecurityConfiguration {

        @Bean
        public SpringSecurityCsrfTokenRepository mailSpyCsrfTokenRepository() {
            SpringSecurityCsrfTokenRepository csrfTokenRepository = new SpringSecurityCsrfTokenRepository();
            csrfTokenRepository.setDelegateCsrfRepository(new HttpSessionCsrfTokenRepository());
            return csrfTokenRepository;
        }

        @Bean
        public MailSpyWebSecurityConfigurer mailSpyWebSecurityConfigurer() {
            return new MailSpyWebSecurityConfigurer();
        }

        public class MailSpyWebSecurityConfigurer extends WebSecurityConfigurerAdapter implements Ordered {

            @Autowired
            private MailSpyProperties properties;

            @Override
            public int getOrder() {
                return properties.getSecurityOrder();
            }

            @Override
            protected void configure(final HttpSecurity http) throws Exception {
                http.regexMatcher(properties.getPathNoTrailingSlash() + "(/.*)?");
                if (properties.isEnableCors()) {
                    final CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
                    corsConfiguration.applyPermitDefaultValues();
                    http.cors().configurationSource(request -> corsConfiguration);
                }
                if (properties.isEnableCsrfProtection()) {
                    http.csrf().csrfTokenRepository(mailSpyCsrfTokenRepository().getDelegateCsrfRepository());
                } else {
                    http.csrf().disable();
                }
            }
        }
    }

    @Configuration
    @ConditionalOnMissingSpringWebSecurity
    public static class MailSpyNoOpWebSecurityConfiguration {

        @Bean
        public NoOpCsrfTokenRepository mailSpyCsrfTokenRepository() {
            return new NoOpCsrfTokenRepository();
        }

    }

}
