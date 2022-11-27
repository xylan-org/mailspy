package org.xylan.mailspy.core.impl.ws;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Listeners(MockitoTestNGListener.class)
public class DisabledSimpAnnotationMethodMessageHandlerTest {

    @Mock
    private SubscribableChannel clientInboundChannel;

    @Mock
    private MessageChannel clientOutboundChannel;

    @Mock
    private SimpMessageSendingOperations brokerTemplate;

    @InjectMocks
    private DisabledSimpAnnotationMethodMessageHandler underTest;

    @Test
    public void isAutoStartupShouldReturnFalse() {
        // GIVEN
        // WHEN
        boolean result = underTest.isAutoStartup();

        // THEN
        assertFalse(result);
    }

}