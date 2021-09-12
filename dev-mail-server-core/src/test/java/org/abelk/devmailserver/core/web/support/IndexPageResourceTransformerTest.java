package org.abelk.devmailserver.core.web.support;

import lombok.SneakyThrows;
import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.apache.commons.io.IOUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class IndexPageResourceTransformerTest {

    @Mock
    private CsrfTokenRepository csrfRepository;

    @InjectMocks
    private IndexPageResourceTransformer underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    @SneakyThrows
    public void testTransformShouldReturnOriginalResourceWhenFileIsNotIndexHtml() {
        // GIVEN
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ResourceTransformerChain transformerChain = Mockito.mock(ResourceTransformerChain.class);
        Resource expected = new InputStreamResource(IOUtils.toInputStream("abcd", StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "totally-not-index.html";
            }
        };

        given(transformerChain.transform(request, expected)).willReturn(expected);

        // WHEN
        Resource actual = underTest.transform(request, expected, transformerChain);

        // THEN
        then(transformerChain).should().transform(request, expected);
        assertEquals(actual, expected);
    }

    @Test
    @SneakyThrows
    public void testTransformShouldReplaceBaseTagAndCsrfTokenInContentWhenFileIsIndexHtml() {
        // GIVEN
        DefaultCsrfToken csrfToken = new DefaultCsrfToken("csrfHeader", "csrfParameter", "testCsrfToken");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ResourceTransformerChain transformerChain = Mockito.mock(ResourceTransformerChain.class);
        Resource resource = new InputStreamResource(IOUtils.toInputStream(
                "<base href=\"randomStuff\"><meta name=\"csrf_token\" content=\"randomStuff\">",
                StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "index.html";
            }
            @Override
            public long lastModified() {
                return 0;
            }
        };

        String expectedContent = "<base href=\"/test-path/resources/\">" +
                "<meta name=\"csrf_token\" content=\"testCsrfToken\">";
        TransformedResource expected = new TransformedResource(resource, expectedContent.getBytes(StandardCharsets.UTF_8));

        given(csrfRepository.generateToken(request)).willReturn(csrfToken);
        given(transformerChain.transform(request, resource)).willReturn(resource);

        // WHEN
        Resource actual = underTest.transform(request, resource, transformerChain);

        // THEN
        then(transformerChain).should().transform(request, resource);
        then(csrfRepository).should().saveToken(csrfToken, request, null);
        assertEquals(actual, expected);
    }

    private DevMailServerProperties createProperties() {
        DevMailServerProperties properties = new DevMailServerProperties();
        properties.setPath("/test-path");
        return properties;
    }

}