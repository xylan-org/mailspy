package org.abelk.devmailserver.core.web.forward;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Setter;
import org.abelk.devmailserver.core.config.DevMailServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardIndexController {

    @Setter
    @Autowired
    private DevMailServerProperties properties;

    @RequestMapping
    public void forwardIndexRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext()
                .getRequestDispatcher(properties.getPathNoTrailingSlash() + "/resources/index.html")
                .forward(request, response);
    }

}
