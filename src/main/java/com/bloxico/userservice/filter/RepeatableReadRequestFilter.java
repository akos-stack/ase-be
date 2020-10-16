package com.bloxico.userservice.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RepeatableReadRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // request body, being an input stream, can be read only once
        // wrapper will make a copy of input stream each time we try to access it so we don't encounter exception
        RepeatableReadRequestWrapper repeatableReadRequestWrapper = new RepeatableReadRequestWrapper(request);
        filterChain.doFilter(repeatableReadRequestWrapper, response);
    }
}
