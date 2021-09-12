package org.abelk.devmailserver.core.web.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;
import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

@Component
public class IndexPageResourceTransformer implements ResourceTransformer {

    private static final String INDEX_PAGE_FILENAME = "index.html";

    @Setter
    @Autowired
    private DevMailServerProperties properties;

    @Qualifier("dmsCsrfTokenRepository")
    @Autowired
    private CsrfTokenRepository csrfRepository;

    @Override
    public Resource transform(final HttpServletRequest request, final Resource originalResource, final ResourceTransformerChain transformerChain) throws IOException {
        Resource result;

        final Resource resource = transformerChain.transform(request, originalResource);
        if (resource.getFilename().equals(INDEX_PAGE_FILENAME)) {
            final byte[] bytes = readToString(resource.getInputStream())
                    .replaceFirst("<base href=\".*?\">", "<base href=\"" + properties.getPathNoTrailingSlash() + "/resources/\">")
                    .replaceFirst("<meta name=\"csrf_token\" content=\".*?\">",
                            "<meta name=\"csrf_token\" content=\"" + createAndSaveCsrfToken(request) + "\">")
                    .getBytes();
            result = new TransformedResource(resource, bytes);
        } else {
            result = resource;
        }

        return result;
    }

    private String createAndSaveCsrfToken(final HttpServletRequest request) {
        final CsrfToken token = csrfRepository.generateToken(request);
        csrfRepository.saveToken(token, request, null);
        return token.getToken();
    }

    private String readToString(final InputStream inputStream) {
        String text;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

}
