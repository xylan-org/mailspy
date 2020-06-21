package org.abelk.devmailserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties
public class AppProperties {

    private boolean cors;

}
