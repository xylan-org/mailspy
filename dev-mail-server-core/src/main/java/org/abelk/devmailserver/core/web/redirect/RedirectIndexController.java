package org.abelk.devmailserver.core.web.redirect;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;

public class RedirectIndexController {

    private final String baseUrl;

    public RedirectIndexController(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @HandlerMethod
    public void createSseEmitter(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext()
                .getRequestDispatcher(baseUrl + "/resources/index.html")
                .forward(request, response);
    }

}
