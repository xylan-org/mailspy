package org.xylan.mailspy.core.impl.web.support;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.support.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
public class MailSpyIndexPageResourceTransformer implements ResourceTransformer {

    private static final String INDEX_PAGE_FILENAME = "index.html";

    @Setter
    @Autowired
    private MailSpyProperties properties;

    @Autowired
    @Qualifier("mailSpyCsrfTokenRepository")
    private CsrfTokenRepository csrfTokenRepository;

    @Override
    public Resource transform(final HttpServletRequest request, final Resource originalResource, final ResourceTransformerChain transformerChain) throws IOException {
        Resource result;

        final Resource resource = transformerChain.transform(request, originalResource);
        if (resource.getFilename().equals(INDEX_PAGE_FILENAME)) {
            final byte[] bytes = readToString(resource.getInputStream())
                .replaceFirst("<base href=\".*?\">", "<base href=\"" + properties.getPathNoTrailingSlash() + "/resources/\">")
                .replaceFirst("<meta name=\"csrf_token\" content=\".*?\">",
                    "<meta name=\"csrf_token\" content=\"" + csrfTokenRepository.getCsrfToken(request) + "\">")
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
