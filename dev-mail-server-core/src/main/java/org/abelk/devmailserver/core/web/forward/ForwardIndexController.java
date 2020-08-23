package org.abelk.devmailserver.core.web.forward;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.abelk.devmailserver.core.web.handlermapping.HandlerMethod;

public class ForwardIndexController {

    private final String baseUrl;

    public ForwardIndexController(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @HandlerMethod
    public void forwardIndexRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext()
                .getRequestDispatcher(baseUrl + "/resources/index.html")
                .forward(request, response);
    }

}
