package org.xylan.mailspy.core.web.forward;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xylan.mailspy.core.config.MailSpyProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MailSpyForwardIndexController {

    @Setter
    @Autowired
    private MailSpyProperties properties;

    @RequestMapping
    public void forwardIndexRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext()
                .getRequestDispatcher(properties.getPathNoTrailingSlash() + "/resources/index.html")
                .forward(request, response);
    }

}
