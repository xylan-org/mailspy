package org.xylan.mailspy.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;

@Configuration
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
@ConditionalOnMailSpyEnabled
@ConditionalOnProperty(name = "mailspy.sender.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(JavaMailSender.class)
public class MailSpySenderAutoConfiguration {

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
