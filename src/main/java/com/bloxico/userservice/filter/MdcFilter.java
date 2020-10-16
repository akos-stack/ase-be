package com.bloxico.userservice.filter;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            // TODO modify this one so it shows request id
            MDC.put("reqId", httpRequest.getHeader("request-id"));
            MDC.put("userAgent", httpRequest.getHeader("user-agent"));
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}