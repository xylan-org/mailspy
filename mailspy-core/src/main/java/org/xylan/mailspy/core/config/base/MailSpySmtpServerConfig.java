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

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.subetha.EventPublishingMessageHandler;

/**
 * Configuration for MailSpy's embedded SMTP server.
 */
@Configuration
public class MailSpySmtpServerConfig {

    @Autowired
    private MailSpyProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer mailSpySmtpServer() {
        final SMTPServer smtpServer = new SMTPServer(context -> mailSpyMessageHandler());
        smtpServer.setPort(properties.getSmtpPort());
        smtpServer.setBindAddress(properties.getSmtpBindAddress());
        return smtpServer;
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public MessageHandler mailSpyMessageHandler() {
        return new EventPublishingMessageHandler();
    }
}
