package org.xylan.mailspy.core.impl.web.forward;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xylan.mailspy.core.config.properties.MailSpyProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MailSpyForwardIndexController {

    @Setter
    @Autowired
    private MailSpyProperties properties;

    @RequestMapping
    @SneakyThrows
    public void forwardIndexRequest(final HttpServletRequest request, final HttpServletResponse response) {
        request.getServletContext()
                .getRequestDispatcher(properties.getPathNoTrailingSlash() + "/resources/index.html")
                .forward(request, response);
    }

}
