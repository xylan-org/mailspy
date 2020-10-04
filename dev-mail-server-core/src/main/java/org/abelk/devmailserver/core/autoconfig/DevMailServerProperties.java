package org.abelk.devmailserver.core.autoconfig;

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
    private int port = 2525;

    @NotNull
    private InetAddress bindAddress = InetAddress.getLoopbackAddress();

    @NotNull
    private WebUiProperties webUi = new WebUiProperties();

    @Data
    public static class WebUiProperties {

        @NotEmpty
        private String url = "/dms";
        
        @Min(1)
        private int retainEmails = 10;

    }

}
