package org.xylan.mailspy.core.web.support.csrf;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.testng.Assert.*;

@Listeners(MockitoTestNGListener.class)
public class NoOpCsrfTokenRepositoryTest {

    @InjectMocks
    private NoOpCsrfTokenRepository underTest;

    @Test
    public void testGetCsrfTokenShouldReturnEmptyString() {
        // GIVEN
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String expected = "";

        // WHEN
        String actual = underTest.getCsrfToken(request);

        // THEN
        assertEquals(actual, expected);
    }

}