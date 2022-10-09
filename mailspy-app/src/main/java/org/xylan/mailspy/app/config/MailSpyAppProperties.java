package org.xylan.mailspy.app.config;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mailspy.app")
public class MailSpyAppProperties {

    private Demo demo;

    @Data
    public static class Demo {
        private boolean enabled = false;
        private String mailsDirectory;
        private int mailsFrequencyMs = 10000;
        private List<String> emails = Collections.emptyList();
        private List<String> subjects = Collections.emptyList();
    }

}
