package org.abelk.devmailserver.core.web.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

public class BasePathResourceTransformer implements ResourceTransformer {

    private final String fileName;
    private final String basePath;

    public BasePathResourceTransformer(final String fileName, final String basePath) {
        this.fileName = fileName;
        this.basePath = basePath;
    }

    @Override
    public Resource transform(final HttpServletRequest request, final Resource originalResource, final ResourceTransformerChain transformerChain) throws IOException {
        Resource result;

        final Resource resource = transformerChain.transform(request, originalResource);
        if (resource.getFilename().equals(fileName)) {
            final byte[] bytes = readToString(resource.getInputStream())
                    .replaceFirst("<base href=\".*?\">", "<base href=\"" + basePath + "\">")
                    .getBytes();
            result = new TransformedResource(resource, bytes);
        } else {
            result = resource;
        }

        return result;
    }

    private String readToString(final InputStream inputStream) {
        String text;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

}
