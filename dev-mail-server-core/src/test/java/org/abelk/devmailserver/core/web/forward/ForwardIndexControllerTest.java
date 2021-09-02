package org.abelk.devmailserver.core.web.forward;

import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Listeners(MockitoTestNGListener.class)
public class ForwardIndexControllerTest {

    @InjectMocks
    private ForwardIndexController underTest;

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

    private DevMailServerProperties createProperties() {
        DevMailServerProperties properties = new DevMailServerProperties();
        properties.setPath("/test-path");
        return properties;
    }
}
