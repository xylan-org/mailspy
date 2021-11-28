package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;
import org.xylan.mailspy.core.impl.subetha.EventPublishingMessageHandler;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@ConditionalOnMailSpyEnabled
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

}
