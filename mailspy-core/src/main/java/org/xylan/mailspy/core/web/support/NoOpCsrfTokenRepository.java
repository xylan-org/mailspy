package org.xylan.mailspy.core.web.support;

import lombok.Getter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoOpCsrfTokenRepository implements CsrfTokenRepository {

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new EmptyCsrfToken();
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        // ignored
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return new EmptyCsrfToken();
    }

    @Getter
    private class EmptyCsrfToken implements CsrfToken {
        private String headerName = "";
        private String parameterName;
        private String token;
    }

}
