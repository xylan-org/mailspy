/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.core.impl.web.support;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.web.index.resourcetransformer.MailSpyIndexPageResourceTransformer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailSpyIndexPageResourceTransformerTest {

    @Mock
    private ServletContext servletContext;

    @InjectMocks
    private MailSpyIndexPageResourceTransformer underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    @SneakyThrows
    public void transformShouldReturnOriginalResourceWhenFileIsNotIndexHtml() {
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
    public void transformShouldReplaceBaseTagInContentWhenFileIsIndexHtml() {
        // GIVEN
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ResourceTransformerChain transformerChain = Mockito.mock(ResourceTransformerChain.class);
        Resource resource =
                new InputStreamResource(
                        IOUtils.toInputStream("<base href=\"randomStuff\" />", StandardCharsets.UTF_8)) {
                    @Override
                    public String getFilename() {
                        return "index.html";
                    }

                    @Override
                    public long lastModified() {
                        return 0;
                    }
                };

        String expectedContent = "<base href=\"/context-path/test-path/resources/\" />";

        given(servletContext.getContextPath()).willReturn("/context-path");
        given(transformerChain.transform(request, resource)).willReturn(resource);

        TransformedResource expected =
                new TransformedResource(resource, expectedContent.getBytes(StandardCharsets.UTF_8));

        // WHEN
        Resource actual = underTest.transform(request, resource, transformerChain);

        // THEN
        then(transformerChain).should().transform(request, resource);
        assertEquals(actual, expected);
    }

    private MailSpyProperties createProperties() {
        MailSpyProperties properties = new MailSpyProperties();
        properties.setPath("/test-path");
        return properties;
    }
}
