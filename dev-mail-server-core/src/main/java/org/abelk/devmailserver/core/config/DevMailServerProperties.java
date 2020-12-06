package org.abelk.devmailserver.core.config;

import java.net.InetAddress;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "devmailserver")
public class DevMailServerProperties {

    @Min(1024)
    @Max(65535)
    private int smtpPort = 2525;

    @NotNull
    private InetAddress bindAddress = InetAddress.getLoopbackAddress();

    @NotEmpty
    private String path = "/dms";

    @Min(1)
    private int retainEmails = 10;

    private boolean enableCors = false;

    private boolean enableCsrfProtection = false;

    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

}
