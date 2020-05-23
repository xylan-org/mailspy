package org.abelk.devmailserver.core.web.resources;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class WebResourceBundle {

    private String classpathPrefix;
    private List<String> js;
    private List<String> css;

    public List<String> getJsWithPrefix(final String prefix) {
        return getBundleWithPrefix(js, prefix);
    }

    public List<String> getCssWithPrefix(final String prefix) {
        return getBundleWithPrefix(css, prefix);
    }

    private List<String> getBundleWithPrefix(final List<String> bundle, final String prefix) {
        return bundle.stream()
                .map(s -> prefix + s)
                .collect(Collectors.toList());
    }

}
