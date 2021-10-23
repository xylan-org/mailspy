package org.xylan.mailspy.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;

@Data
@ConfigurationProperties(prefix = "mailspy")
public class MailSpyProperties {

    @Min(1)
    @Max(65535)
    private int smtpPort = 2525;

    @NotNull
    private InetAddress bindAddress = InetAddress.getLoopbackAddress();

    @NotEmpty
    private String path = "/mailspy";

    @Min(1)
    private int retainEmails = 10;

    private int securityOrder = 99;

    private boolean enableCors = false;

    private boolean enableCsrfProtection = true;

    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

}
