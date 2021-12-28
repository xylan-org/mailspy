package org.xylan.mailspy.core.config.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.subetha.EventPublishingMessageHandler;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class MailSpySmtpServerConfig {

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
