package org.xylan.mailspy.core.impl.web.support;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.xylan.mailspy.core.config.MailSpyProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
public class MailSpyIndexPageResourceTransformer implements ResourceTransformer {

    private static final String INDEX_PAGE_FILENAME = "index.html";

    @Setter
    @Autowired
    private MailSpyProperties properties;

    @Override
    @SneakyThrows
    public Resource transform(final HttpServletRequest request, final Resource originalResource, final ResourceTransformerChain transformerChain) {
        Resource result;
        final Resource resource = transformerChain.transform(request, originalResource);
        if (resource.getFilename().equals(INDEX_PAGE_FILENAME)) {
            String resourceAsString = injectBasePath(readToString(resource.getInputStream()));
            result = new TransformedResource(resource, resourceAsString.getBytes());
        } else {
            result = resource;
        }
        return result;
    }

    private String injectBasePath(String resourceAsString) {
        return resourceAsString.replaceFirst(
            "<base href=\".*?\"\\s*/?>",
            "<base href=\"" + properties.getPathNoTrailingSlash() + "/resources/\" />");
    }

    private String readToString(final InputStream inputStream) {
        String text;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

}
