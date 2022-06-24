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

package org.xylan.mailspy.core.impl.web.history.controller;

import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.core.impl.web.history.storage.MailSpyHistoryStorage;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailSpyHistoryControllerTest {

    @Mock
    private MailSpyHistoryStorage mailSpyHistoryStorage;

    @InjectMocks
    private MailSpyHistoryController underTest;

    @Test
    public void getMailsHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        List<MailSpyEmail> expected = List.of(MailSpyEmail.builder().build());
        given(mailSpyHistoryStorage.getHistory()).willReturn(expected);

        // WHEN
        List<MailSpyEmail> actual = underTest.getMailsHistory();

        // THEN
        assertEquals(actual, expected);
    }

    @Test
    public void clearHistoryShouldDelegateToMailsHistoryStorage() {
        // GIVEN
        // WHEN
        underTest.clearHistory();

        // THEN
        then(mailSpyHistoryStorage).should().clearHistory();
    }

}
