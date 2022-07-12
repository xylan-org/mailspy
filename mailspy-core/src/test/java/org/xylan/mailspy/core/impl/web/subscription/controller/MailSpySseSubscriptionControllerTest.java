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

package org.xylan.mailspy.core.impl.web.subscription.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.web.subscription.sse.MailSpySseEmitterRegistry;

@Listeners(MockitoTestNGListener.class)
public class MailSpySseSubscriptionControllerTest {

    @Mock
    private MailSpySseEmitterRegistry mailSpySseEmitterRegistry;

    @InjectMocks
    private MailSpySseSubscriptionController underTest;

    @Test
    public void createSseEmitterShouldReturnResultOfSseEmitterRegistry() {
        // GIVEN
        SseEmitter expected = mock(SseEmitter.class);
        given(mailSpySseEmitterRegistry.createEmitter()).willReturn(expected);

        // WHEN
        SseEmitter actual = underTest.createSseEmitter();

        // THEN
        assertEquals(actual, expected);
    }
}
