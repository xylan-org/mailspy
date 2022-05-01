package org.xylan.mailspy.core.impl.web.forward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xylan.mailspy.core.config.MailSpyProperties;

/**
 * Controller that forwards requests for MailSpy's root path to the index page.
 */
@Controller
public class MailSpyForwardIndexController {

    @Setter
    @Autowired
    private MailSpyProperties properties;

    /**
     * Forwards requests to the index page.
     * @param request The HTTP request object.
     * @param response The HTTP response object.
     */
    @SneakyThrows
    @RequestMapping(produces = MediaType.APPLICATION_XHTML_XML_VALUE)
    public void forwardIndexRequest(final HttpServletRequest request, final HttpServletResponse response) {
        request.getServletContext()
            .getRequestDispatcher(properties.getPathNoTrailingSlash() + "/resources/index.html")
            .forward(request, response);
    }

}
