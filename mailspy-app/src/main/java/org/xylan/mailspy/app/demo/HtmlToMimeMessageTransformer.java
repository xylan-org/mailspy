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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

/**
 * Creates {@link MimeMessage} from HTML mail templates.
 */
@Component
public class HtmlToMimeMessageTransformer {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("<img.*src=\"(.+?)\".*\\/>");

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private HtmlTextExtractor htmlTextExtractor;

    /**
     * Creates a {@link MimeMessage} from the given HTML mail template.
     * Paths will be extracted from img tags and will be resolved relative to the HTML file's parent directory.
     * Then, they'll be replaced with content ID references, and added as parts to the MIME message with the same identifiers.
     *
     * @param htmlMail The HTML mail template file.
     * @return The created MIME message.
     */
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
