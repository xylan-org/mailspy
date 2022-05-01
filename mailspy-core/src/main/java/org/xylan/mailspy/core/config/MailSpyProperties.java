package org.xylan.mailspy.core.config;

import java.net.InetAddress;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties of MailSpy.
 */
@Data
@ConfigurationProperties(prefix = "mailspy")
public class MailSpyProperties {

    private int smtpPort = 2525;
    private InetAddress smtpBindAddress = InetAddress.getLoopbackAddress();
    private String path = "/mailspy";
    private int retainEmails = 100;
    private boolean enableCors;

    /**
     * Returns the path without trailing slashes.
     * @return The trimmed path.
     */
    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

}
