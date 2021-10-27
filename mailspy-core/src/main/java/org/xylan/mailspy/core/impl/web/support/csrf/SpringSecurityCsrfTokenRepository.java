package org.xylan.mailspy.core.impl.web.support.csrf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.HttpServletRequest;

public class SpringSecurityCsrfTokenRepository implements CsrfTokenRepository {

    @Setter
    @Getter
    private org.springframework.security.web.csrf.CsrfTokenRepository delegateCsrfRepository;

    @Override
    public String getCsrfToken(final HttpServletRequest request) {
        final CsrfToken token = delegateCsrfRepository.generateToken(request);
        delegateCsrfRepository.saveToken(token, request, null);
        return token.getToken();
    }

}
