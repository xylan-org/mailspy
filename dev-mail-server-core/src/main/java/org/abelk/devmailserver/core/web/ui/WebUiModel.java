package org.abelk.devmailserver.core.web.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebUiModel {

    private String path;
    private String theme;

}
