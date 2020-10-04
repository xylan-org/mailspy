package org.abelk.devmailserver.core.autoconfig;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.Map;

import org.abelk.devmailserver.core.subetha.EventPublishingMessageHandler;
import org.abelk.devmailserver.core.web.forward.ForwardIndexController;
import org.abelk.devmailserver.core.web.history.controller.MailsHistoryController;
import org.abelk.devmailserver.core.web.subscription.controller.MailsSubscriptionController;
import org.abelk.devmailserver.core.web.support.handlermapping.SimpleUrlHandlerMethodMapping;
import org.abelk.devmailserver.core.web.support.transformer.BasePathResourceTransformer;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
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
        private ForwardIndexController forwardIndexController;

        @Autowired
        private MailsSubscriptionController mailsSubscriptionController;

        @Autowired
        private MailsHistoryController mailsHistoryController;

        private final String basePath;

        @Autowired
        public DevMailServerWebMvcConfigurer(final DevMailServerProperties properties) {
            basePath = properties.getWebUi().getUrl();
        }

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.setOrder(0)
                    .addResourceHandler(basePath + "/resources/**")
                    .addResourceLocations("classpath:META-INF/dms-frontend/")
                    .resourceChain(true)
                    .addTransformer(new BasePathResourceTransformer("index.html", basePath + "/resources/"));
        }

        @Bean
        public SimpleUrlHandlerMapping devMailServerHandlerMapping() {
            final SimpleUrlHandlerMethodMapping handlerMapping = new SimpleUrlHandlerMethodMapping(Map.of(
                    basePath, forwardIndexController,
                    basePath + "/mails/subscribe", mailsSubscriptionController,
                    basePath + "/mails/history", mailsHistoryController));
            handlerMapping.setOrder(1);
            return handlerMapping;
        }

    }

}
