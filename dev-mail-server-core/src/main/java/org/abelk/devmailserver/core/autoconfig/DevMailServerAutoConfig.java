package org.abelk.devmailserver.core.autoconfig;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.Map;

import org.abelk.devmailserver.core.mailserver.EventPublishingMessageHandler;
import org.abelk.devmailserver.core.web.handlermapping.SimpleUrlHandlerMethodMapping;
import org.abelk.devmailserver.core.web.redirect.RedirectIndexController;
import org.abelk.devmailserver.core.web.sse.SseSubscriptionController;
import org.abelk.devmailserver.core.web.transformer.BasePathResourceTransformer;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.MimeConfig;
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

import tech.blueglacier.parser.CustomContentHandler;

@Configuration
@ConditionalOnProperty(name = "devmailserver.enabled", havingValue = "true")
@EnableConfigurationProperties(DevMailServerProperties.class)
@ComponentScan("org.abelk.devmailserver.core.mailserver")
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

    @Bean
    public MimeStreamParser mime4jParser() {
        final MimeStreamParser mime4jParser = new MimeStreamParser(MimeConfig.DEFAULT, DecodeMonitor.SILENT, new DefaultBodyDescriptorBuilder());
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(blueglacierContentHandler());
        return mime4jParser;
    }

    @Bean
    public CustomContentHandler blueglacierContentHandler() {
        return new CustomContentHandler();
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
    @ComponentScan("org.abelk.devmailserver.core.web")
    public static class DevMailServerWebMvcConfigurer implements WebMvcConfigurer {

        private final DevMailServerProperties.WebUiProperties properties;

        @Autowired
        public DevMailServerWebMvcConfigurer(final DevMailServerProperties properties) {
            this.properties = properties.getWebUi();
        }

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.addResourceHandler(properties.getUrl() + "/resources/**")
                    .addResourceLocations("classpath:META-INF/dms-frontend/")
                    .resourceChain(true)
                    .addTransformer(new BasePathResourceTransformer("index.html", properties.getUrl() + "/resources/"));
        }

        @Bean
        public SimpleUrlHandlerMapping devMailServerHandlerMapping() {
            return new SimpleUrlHandlerMethodMapping(Map.of(
                    properties.getUrl(), redirectIndexController(),
                    properties.getUrl() + "/subscribe", sseSubscribeController()));
        }

        @Bean
        public RedirectIndexController redirectIndexController() {
            return new RedirectIndexController(properties.getUrl());
        }

        @Bean
        public SseSubscriptionController sseSubscribeController() {
            return new SseSubscriptionController();
        }

    }

}
