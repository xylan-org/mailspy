package org.xylan.mailspy.app.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.xylan.mailspy.app.demo.exception.MailSpyDemoException;

@Component
public class HtmlToMimeMessageTransformer {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("<img.*src=\"(.+?)\".*\\/>");

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private HtmlTextExtractor htmlTextExtractor;

    public MimeMessage transform(File htmlMail) {
        Path parentPath = htmlMail.toPath().getParent();
        String htmlMailContent = fileToString(htmlMail);
        Map<String, File> contentIdToFile = new HashMap<>();
        Matcher matcher = IMAGE_PATTERN.matcher(htmlMailContent);
        while (matcher.find()) {
            String imageSrc = matcher.group(1);
            File imageFile = parentPath.resolve(imageSrc).toFile();
            String uuid = UUID.randomUUID().toString();
            contentIdToFile.put(uuid, imageFile);
            htmlMailContent = htmlMailContent.replaceAll(Pattern.quote(imageSrc), "cid:" + uuid);
        }
        return buildMimeMessage(htmlMailContent, contentIdToFile);
    }

    private MimeMessage buildMimeMessage(String htmlMailContent, Map<String, File> contentIdToFile) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setText(htmlTextExtractor.extractText(htmlMailContent), htmlMailContent);
            for (Map.Entry<String, File> entry : contentIdToFile.entrySet()) {
                helper.addInline(entry.getKey(), entry.getValue());
            }
            return message;
        } catch (MessagingException exception) {
            throw new MailSpyDemoException("Failed to construct message", exception);
        }
    }

    private String fileToString(File file) {
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException exception) {
            throw new MailSpyDemoException("Failed to open file " + file.getName(), exception);
        }
    }

}
