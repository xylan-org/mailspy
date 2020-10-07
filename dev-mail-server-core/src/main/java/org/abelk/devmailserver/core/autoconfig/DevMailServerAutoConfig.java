package org.abelk.devmailserver.core.autoconfig;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.List;

import org.abelk.devmailserver.core.subetha.EventPublishingMessageHandler;
import org.abelk.devmailserver.core.web.support.IndexPageResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;

@Configuration
@ConditionalOnProperty(name = "devmailserver.enabled", havingValue = "true")
@EnableConfigurationProperties(DevMailServerProperties.class)
@ComponentScan("org.abelk.devmailserver.core")
public class DevMailServerAutoConfig {

    @Autowired
    private DevMailServerProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer smtpServer() {
        final SMTPServer smtpServer = new SMTPServer(context -> messageHandler());
        smtpServer.setPort(properties.getPort());
        smtpServer.setBindAddress(properties.getBindAddress());
        return smtpServer;
    }

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public MessageHandler messageHandler() {
        return new EventPublishingMessageHandler();
    }

    @Configuration
    @AutoConfigureAfter(MailSenderAutoConfiguration.class)
    @ConditionalOnProperty(name = "devmailserver.sender.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(JavaMailSender.class)
    public static class DevMailSenderAutoConfig {

        @Autowired
        private DevMailServerProperties properties;

        @Bean
        public JavaMailSenderImpl mailSender() {
            final JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(properties.getBindAddress().getHostAddress());
            sender.setPort(properties.getPort());
            return sender;
        }

    }

    @EnableWebMvc
    @Configuration
    public static class DevMailServerWebMvcConfigurer implements WebMvcConfigurer {

        @Autowired
        private IndexPageResourceTransformer indexPageResourceTransformer;

        @Autowired
        private DevMailServerProperties properties;

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.addResourceHandler(properties.getWebUi().getUrl() + "/resources/**")
                    .addResourceLocations("classpath:META-INF/dms-frontend/")
                    .resourceChain(true)
                    .addTransformer(indexPageResourceTransformer);
        }

        @Override
        public void configurePathMatch(final PathMatchConfigurer configurer) {
            configurer.addPathPrefix(properties.getWebUi().getUrl(),
                    HandlerTypePredicate.forBasePackage("org.abelk.devmailserver.core"));
        }

    }

    @EnableWebSecurity
    @Configuration
    public class DevMailServerWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

        @Autowired
        private DevMailServerProperties properties;

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.antMatcher(properties.getWebUi().getUrl() + "/**");
            if (properties.getWebUi().isEnableCors()) {
                final CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "DELETE"));
                corsConfiguration.applyPermitDefaultValues();
                http.cors().configurationSource(request -> corsConfiguration);
            }
            if (properties.getWebUi().isEnableCsrfProtection()) {
                http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            } else {
                http.csrf().disable();
            }
        }

    }

}
