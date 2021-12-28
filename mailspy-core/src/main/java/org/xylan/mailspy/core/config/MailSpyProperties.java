package org.xylan.mailspy.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

@Data
@ConfigurationProperties(prefix = "mailspy")
public class MailSpyProperties {

    private int smtpPort = 2525;
    private InetAddress bindAddress = InetAddress.getLoopbackAddress();
    private String path = "/mailspy";
    private int retainEmails = 100;
    private int securityOrder = 99;
    private boolean enableCors = false;
    private boolean enableCsrfProtection = true;

    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

}
