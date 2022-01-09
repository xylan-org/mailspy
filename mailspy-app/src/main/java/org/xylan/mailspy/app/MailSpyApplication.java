package org.xylan.mailspy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@Import(TestWebSecurityConfig.class)
public class MailSpyApplication {

    public static void main(final String[] args) {
        final ApplicationContext applicationContext = SpringApplication.run(MailSpyApplication.class, args);
/*
        final JavaMailSender mailSender = applicationContext.getBean(JavaMailSender.class);

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
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

                // sendCalMsg(mailSender);
            } catch (final MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 15, 15, TimeUnit.SECONDS);
*/
    }

//    private static void sendCalMsg(final JavaMailSender mailSender) throws MailException {
//        try {
//            final MimeMessage message = mailSender.createMimeMessage();
//
//            message.setFrom(new InternetAddress("abc@gmail.com"));
//            message.setSubject("calendar invite");
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress("general_kenobi@example.com"));
//
//            // Create an alternative Multipart
//            final Multipart multipart = new MimeMultipart("alternative");
//
//            final BodyPart descriptionPart = new MimeBodyPart();
//            descriptionPart.setContent("<h1>Hello there!</h1>", "text/html; charset=utf-8");
//            multipart.addBodyPart(descriptionPart);
//
//            // Create the message part
//            final BodyPart messageBodyPart = new MimeBodyPart();
//            // Fill the message
//            messageBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
//            final String iCalContent = "BEGIN:VCALENDAR\n" +
//                    "VERSION:2.0\n" +
//                    "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n" +
//                    "BEGIN:VEVENT\n" +
//                    "UID:uid1@example.com\n" +
//                    "DTSTAMP:19970714T170000Z\n" +
//                    "ORGANIZER;CN=John Doe:MAILTO:john.doe@example.com\n" +
//                    "DTSTART:19970714T170000Z\n" +
//                    "DTEND:19970715T035959Z\n" +
//                    "SUMMARY:Bastille Day Party\n" +
//                    "GEO:48.85299;2.36885\n" +
//                    "END:VEVENT\n" +
//                    "END:VCALENDAR";
//
//            messageBodyPart.setContent(iCalContent, "text/calendar;charset=\"utf-8\";method=REQUEST");
//            multipart.addBodyPart(messageBodyPart);
//
//            // Put parts in message
//            message.setContent(multipart);
//
//            mailSender.send(message);
//
//        } catch (final Throwable exception) {
//            throw new MailSendException("poo-doo happened", exception);
//        }
//    }

}
