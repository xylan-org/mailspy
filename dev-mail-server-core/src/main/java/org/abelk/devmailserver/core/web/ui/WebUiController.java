package org.abelk.devmailserver.core.web.ui;

import java.util.List;

import org.abelk.devmailserver.core.autoconfig.DevMailServerProperties;
import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;
import org.abelk.devmailserver.core.web.resources.WebResource;
import org.springframework.web.bind.annotation.ModelAttribute;

public class WebUiController {

    private static final String DMS_WEB_VIEW_NAME = "dms/index";

    private final WebUiModel model;
    private final List<WebResource> webResources;

    // TODO deal with the theme problem
    public WebUiController(final DevMailServerProperties.WebUiProperties properties, final List<WebResource> webResources) {
        this.webResources = webResources;
        model = WebUiModel.builder()
                .theme(properties.getTheme())
                .path(properties.getUrl())
                .build();
    }

    @ModelAttribute("model")
    public WebUiModel getModel() {
        return model;
    }

    @ModelAttribute("webResources")
    public List<WebResource> getWebResources() {
        return webResources;
    }

    @HandlerMethod
    public String getViewName() {
        return DMS_WEB_VIEW_NAME;
    }

}
