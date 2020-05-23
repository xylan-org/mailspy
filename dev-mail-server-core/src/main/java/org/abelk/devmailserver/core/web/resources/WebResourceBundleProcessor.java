package org.abelk.devmailserver.core.web.resources;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class WebResourceBundleProcessor {

    public List<WebResource> process(final List<WebResourceBundle> bundles, final String prefix) {
        final Map<WebResourceType, List<WebResourceBundle>> groupsByType = bundles.stream()
                .collect(Collectors.groupingBy(r -> r.getType()));

        final List<WebResourceBundle> sortedGroupedBundles = groupsByType.values().stream()
                .flatMap(l -> l.stream().sorted(Comparator.nullsLast(Comparator.comparingInt(b -> b.getOrder()))))
                .collect(Collectors.toList());

        final List<WebResource> resources = new LinkedList<>();
        for (final WebResourceBundle bundle : sortedGroupedBundles) {
            bundle.getFiles().stream()
                    .map(f -> WebResource.builder()
                            .type(bundle.getType())
                            .file(prefix + "/" + bundle.getName() + f)
                            .build())
                    .forEach(resources::add);
        }

        return resources;
    }

}
