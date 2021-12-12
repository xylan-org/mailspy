package org.xylan.mailspy.core.impl.subetha;

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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.BDDMockito.then;

@Listeners(MockitoTestNGListener.class)
public class EventPublishingMessageHandlerTest {

    private static String TEST_UUID = "fdadec6a-222c-45b4-89a0-dd0944deecc3";
    private static Instant TEST_NOW = Instant.parse("2000-01-01T10:15:30.00Z");

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
                .rawMessage(new byte[] { 0x6d, 0x73, 0x67 })
                .timestamp(TEST_NOW)
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
                .timestamp(TEST_NOW)
                .exception(expectedException)
                .build();
        EmailReceivedEvent expected = new EmailReceivedEvent(testMail);

        // WHEN
        underTest.data(messageStream);

        // THEN
        then(applicationEventPublisher).should().publishEvent(expected);
    }

}
