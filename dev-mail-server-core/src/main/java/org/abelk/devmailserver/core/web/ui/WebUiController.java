package org.abelk.devmailserver.core.web.ui;

import org.abelk.devmailserver.core.autoconfig.DevMailServerProperties;
import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;
import org.springframework.web.bind.annotation.ModelAttribute;

public class WebUiController {

    private static final String DMS_WEB_VIEW_NAME = "dms/index";

    private final WebUiModel model;

    public WebUiController(final DevMailServerProperties.WebUiProperties properties) {
        model = WebUiModel.builder()
                .theme(properties.getTheme())
                .path(properties.getUrl())
                .build();
    }

    @ModelAttribute("model")
    public WebUiModel getModel() {
        return model;
    }

    @HandlerMethod
    public String getViewName() {
        return DMS_WEB_VIEW_NAME;
    }

}
