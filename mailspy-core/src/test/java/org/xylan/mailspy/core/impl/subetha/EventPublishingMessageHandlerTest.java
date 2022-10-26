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

package org.xylan.mailspy.core.impl.subetha;

import static org.mockito.BDDMockito.then;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.context.ApplicationEventPublisher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

@Listeners(MockitoTestNGListener.class)
public class EventPublishingMessageHandlerTest {

    private static final String TEST_UUID = "fdadec6a-222c-45b4-89a0-dd0944deecc3";
    private static final ZonedDateTime TEST_NOW = ZonedDateTime.of(2000, 1, 1, 10, 15, 30, 0, ZoneOffset.UTC);
    private static final String TEST_NOW_STRING = "2000-01-01 10:15:30";

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EventPublishingMessageHandler underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setUuidSupplier(() -> UUID.fromString(TEST_UUID));
        underTest.setNowSupplier(() -> TEST_NOW);
    }

    @Test
    public void dataShouldPublishEventWithNormalEmailObjectWhenMessageStreamIsReadable() {
        // GIVEN
        InputStream messageStream = IOUtils.toInputStream("msg", StandardCharsets.UTF_8);
        MailSpyEmail testMail = MailSpyEmail.builder()
                .id(TEST_UUID)
                .rawMessage(new byte[] {0x6d, 0x73, 0x67})
                .timestamp(TEST_NOW_STRING)
                .exception(null)
                .build();
        EmailReceivedEvent expected = new EmailReceivedEvent(testMail);

        // WHEN
        underTest.data(messageStream);

        // THEN
        then(applicationEventPublisher).should().publishEvent(expected);
    }

    @Test
    public void dataShouldPublishEventWithErrorEmailObjectWhenMessageStreamIsNotReadable() {
        // GIVEN
        IOException expectedException = new IOException("oopsie");
        InputStream messageStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw expectedException;
            }
        };
        MailSpyEmail testMail = MailSpyEmail.builder()
                .id(TEST_UUID)
                .rawMessage(null)
                .timestamp(TEST_NOW_STRING)
                .exception(expectedException)
                .build();
        EmailReceivedEvent expected = new EmailReceivedEvent(testMail);

        // WHEN
        underTest.data(messageStream);

        // THEN
        then(applicationEventPublisher).should().publishEvent(expected);
    }
}
