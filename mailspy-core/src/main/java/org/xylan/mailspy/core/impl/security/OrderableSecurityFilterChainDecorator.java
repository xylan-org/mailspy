package org.xylan.mailspy.core.impl.security;

import org.springframework.core.Ordered;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class OrderableSecurityFilterChainDecorator implements SecurityFilterChain, Ordered {

    private final SecurityFilterChain delegateSecurityFilterChain;
    private final int order;

    public OrderableSecurityFilterChainDecorator(SecurityFilterChain delegateSecurityFilterChain, int order) {
        this.delegateSecurityFilterChain = delegateSecurityFilterChain;
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return delegateSecurityFilterChain.matches(request);
    }

    @Override
    public List<Filter> getFilters() {
        return delegateSecurityFilterChain.getFilters();
    }

}
