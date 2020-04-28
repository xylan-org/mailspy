package org.abelk.devmailserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@SpringBootApplication
public class DevMailServerApplication {

    public static void main(final String[] args) {
        final ApplicationContext applicationContext = SpringApplication.run(DevMailServerApplication.class, args);

        final JavaMailSender mailSender = applicationContext.getBean(JavaMailSender.class);

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            System.out.println("Sending shit...");
            final MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper;
            try {
                helper = new MimeMessageHelper(message, true);
                helper.setTo("abc@gmail.com");
                helper.setFrom("xyz@gmail.com");
                helper.setSubject("new email");
                helper.setText("hello", "<h1>hello</h1><br><br><br><br><br><br><br><br><br><br><br><br><br>");
                mailSender.send(message);
            } catch (final MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 15, TimeUnit.SECONDS);

    }

}
