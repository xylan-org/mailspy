package org.xylan.mailspy.core.web.support.csrf;

import javax.servlet.http.HttpServletRequest;

public class NoOpCsrfTokenRepository implements CsrfTokenRepository {

    @Override
    public String getCsrfToken(final HttpServletRequest request) {
        return "";
    }
}
