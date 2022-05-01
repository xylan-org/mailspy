package org.xylan.mailspy.core.impl.web.forward;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.config.MailSpyProperties;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@Listeners(MockitoTestNGListener.class)
public class MailSpyForwardIndexControllerTest {

    @InjectMocks
    private MailSpyForwardIndexController underTest;

    @BeforeMethod
    public void setUp() {
        underTest.setProperties(createProperties());
    }

    @Test
    public void forwardIndexRequestShouldForwardRequestUsingRequestDispatcher() throws ServletException, IOException {
        // GIVEN
        HttpServletRequest request = mock(HttpServletRequest.class, Mockito.RETURNS_DEEP_STUBS);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);

        given(request.getServletContext()
            .getRequestDispatcher("/test-path/resources/index.html")).willReturn(requestDispatcher);

        // WHEN
        underTest.forwardIndexRequest(request, response);

        // THEN
        then(requestDispatcher).should().forward(request, response);
    }

    private MailSpyProperties createProperties() {
        MailSpyProperties properties = new MailSpyProperties();
        properties.setPath("/test-path");
        return properties;
    }
}
