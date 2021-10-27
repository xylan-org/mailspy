package org.xylan.mailspy.core.impl.web.support.csrf;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class SpringSecurityCsrfTokenRepositoryTest {

    @Mock
    private org.springframework.security.web.csrf.CsrfTokenRepository delegateCsrfRepository;

    @InjectMocks
    private SpringSecurityCsrfTokenRepository underTest;

    @Test
    public void testGetCsrfTokenShouldGenerateSaveAndReturnTokenUsingSpringSecurity() {
        // GIVEN
        String expected = "expectedToken";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        CsrfToken csrfToken = new DefaultCsrfToken("headerName", "parameterName", expected);

        given(delegateCsrfRepository.generateToken(request)).willReturn(csrfToken);

        // WHEN
        String actual = underTest.getCsrfToken(request);

        // THEN
        then(delegateCsrfRepository).should().saveToken(csrfToken, request, null);
        assertEquals(actual, expected);
    }

}