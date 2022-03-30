package org.xylan.mailspy.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

@Data
@ConfigurationProperties(prefix = "mailspy")
public class MailSpyProperties {

    private int smtpPort = 2525;
    private InetAddress smtpBindAddress = InetAddress.getLoopbackAddress();
    private String path = "/mailspy";
    private int retainEmails = 100;

    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

}
