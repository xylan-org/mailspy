package org.abelk.devmailserver.core.web.ui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.abelk.devmailserver.core.autoconfig.DevMailServerProperties;
import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;
import org.abelk.devmailserver.core.web.resources.WebResourceBundle;
import org.springframework.web.bind.annotation.ModelAttribute;

public class WebUiController {

    private static final String DMS_WEB_VIEW_NAME = "dms/index";

    private final WebUiModel model;
    private final Map<String, WebResourceBundle> bundles;

    // TODO deal with the theme problem
    public WebUiController(final DevMailServerProperties.WebUiProperties properties, final Map<String, WebResourceBundle> bundles) {
        model = WebUiModel.builder()
                .theme(properties.getTheme())
                .path(properties.getUrl())
                .build();
        this.bundles = bundles;
    }

    @ModelAttribute("model")
    public WebUiModel getModel() {
        return model;
    }

    @ModelAttribute("jsResources")
    public List<String> getJsResources() {
        return bundles.entrySet()
                .stream()
                .map(b -> b.getValue().getJsWithPrefix(model.getPath() + "/" + b.getKey()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @ModelAttribute("cssResources")
    public List<String> getCssResources() {
        return bundles.entrySet()
                .stream()
                .map(b -> b.getValue().getCssWithPrefix(model.getPath() + "/" + b.getKey()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @HandlerMethod
    public String getViewName() {
        return DMS_WEB_VIEW_NAME;
    }

}
