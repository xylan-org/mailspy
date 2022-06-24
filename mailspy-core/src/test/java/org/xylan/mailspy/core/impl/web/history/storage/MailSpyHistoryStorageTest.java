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

package org.xylan.mailspy.core.impl.web.history.storage;

import java.util.Collections;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.MailSpyProperties;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;

import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class MailSpyHistoryStorageTest {

    @InjectMocks
    private MailSpyHistoryStorage underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    public void addEmailShouldAddEmailToQueueWhenLimitIsNotYetReached() {
        // GIVEN
        MailSpyEmail email = MailSpyEmail.builder().id("id").build();

        // WHEN
        underTest.addEmail(email);

        // THEN
        assertEquals(underTest.getHistory().get(0), email);
    }

    @Test
    public void addEmailShouldAddEmailToQueueAndRemoveFirstWhenLimitIsReached() {
        // GIVEN
        MailSpyEmail email1 = MailSpyEmail.builder().id("uno").build();
        MailSpyEmail email2 = MailSpyEmail.builder().id("dos").build();
        MailSpyEmail email3 = MailSpyEmail.builder().id("tres").build();
        underTest.addEmail(email1);
        underTest.addEmail(email2);

        // WHEN
        underTest.addEmail(email3);

        // THEN
        assertEquals(underTest.getHistory(), List.of(email2, email3));
    }

    @Test
    public void clearHistoryShouldClearQueue() {
        // GIVEN
        MailSpyEmail email = MailSpyEmail.builder().id("id").build();
        underTest.addEmail(email);

        // WHEN
        underTest.clearHistory();

        // THEN
        assertEquals(underTest.getHistory(), Collections.emptyList());
    }

    private MailSpyProperties createProperties() {
        MailSpyProperties properties = new MailSpyProperties();
        properties.setRetainEmails(2);
        return properties;
    }

}
