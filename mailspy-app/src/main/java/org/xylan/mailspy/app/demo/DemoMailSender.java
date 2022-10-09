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

package org.xylan.mailspy.app.demo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.app.config.MailSpyAppProperties;
import org.xylan.mailspy.app.demo.exception.MailSpyDemoException;

/**
 * Implementation of the mail sending for the demo mode.
 */
@Component
@DependsOn("mailSpySmtpServer")
public class DemoMailSender {

    @Autowired
    private MailSpyAppProperties properties;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private HtmlToMimeMessageTransformer htmlToMimeMessageTransformer;

    /**
     * Reads the mail template html files from the configured directory and sends
     * one of them randomly periodically.
     */
    @PostConstruct
    public void initialize() {
        if (properties.getDemo().isEnabled()) {
            File mailsDirectory = new File(properties.getDemo().getMailsDirectory());
            validateValuesConfig();
            validateMailsDirectoryConfig(mailsDirectory);
            scheduleMailSending(mailsDirectory);
        }
    }

    private void validateMailsDirectoryConfig(File mailsDirectory) {
        if (!mailsDirectory.exists() || !mailsDirectory.isDirectory()) {
            throw new MailSpyDemoException("Demo mails directory should be an existing directory!");
        }
        if (listFiles(mailsDirectory).findAny().isEmpty()) {
            throw new MailSpyDemoException("Configured demo mails directory contains no .html files!");
        }
    }

    private void validateValuesConfig() {
        if (properties.getDemo().getEmails().isEmpty()) {
            throw new MailSpyDemoException("No testing email addresses were defined!");
        }
        if (properties.getDemo().getSubjects().isEmpty()) {
            throw new MailSpyDemoException("No testing subjects were defined!");
        }
    }

    private void scheduleMailSending(File mailsDirectory) {
        List<File> demoMails = listFiles(mailsDirectory).collect(Collectors.toList());
        taskScheduler.schedule(
                () -> sendDemoEmail(demoMails),
                new PeriodicTrigger(properties.getDemo().getMailsFrequencyMs()));
    }

    private void sendDemoEmail(List<File> demoMails) {
        File demoMail = pickRandomFile(demoMails);
        try {
            MimeMessage message = htmlToMimeMessageTransformer.transform(demoMail);
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(pickRandomEmail());
            helper.setFrom(pickRandomEmail());
            helper.setSubject(pickRandomSubject());
            javaMailSender.send(message);
        } catch (MessagingException exception) {
            throw new MailSpyDemoException("Failed to set headers on message", exception);
        }
    }

    private File pickRandomFile(List<File> demoMails) {
        int randomIndex = new Random().nextInt(demoMails.size());
        return demoMails.get(randomIndex);
    }

    private String pickRandomEmail() {
        List<String> emails = properties.getDemo().getEmails();
        int randomIndex = new Random().nextInt(emails.size());
        return emails.get(randomIndex);
    }

    private String pickRandomSubject() {
        List<String> emails = properties.getDemo().getSubjects();
        int randomIndex = new Random().nextInt(emails.size());
        return emails.get(randomIndex);
    }

    private Stream<File> listFiles(File directory) {
        Stream<File> result = Stream.empty();
        File[] files = directory.listFiles();
        if (files != null) {
            result = Arrays.stream(files).filter(file -> !file.isDirectory()).filter(file -> file.getName()
                    .endsWith(".html"));
        }
        return result;
    }
}
