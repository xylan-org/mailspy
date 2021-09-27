package org.xylan.mailspy.core.web.support.csrf;

import javax.servlet.http.HttpServletRequest;

public interface CsrfTokenRepository {

    String getCsrfToken(HttpServletRequest request);

}
