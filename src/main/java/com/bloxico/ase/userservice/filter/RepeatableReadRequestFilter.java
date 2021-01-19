package com.bloxico.ase.userservice.filter;

import org.apache.commons.io.IOUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RepeatableReadRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {
        // request body, being an input stream, can be read only once
        // wrapper will make a copy of input stream each time we try to access it so we don't encounter exception
        var repeatableReadRequestWrapper = new RepeatableReadRequestWrapper(request);
        filterChain.doFilter(repeatableReadRequestWrapper, response);
    }

    /**
     * {@link HttpServletRequest#getInputStream()} can be read only once.
     * This wrapper makes a copy of it each time read is attempted so we don't encounter exception.
     */
    private static final class RepeatableReadRequestWrapper extends HttpServletRequestWrapper {

        private byte[] requestBody;

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final var byteArrayInputStream = new ByteArrayInputStream(getRequestBody());
            return new ServletInputStream() {

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                }

            };
        }

        private RepeatableReadRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private byte[] getRequestBody() throws IOException {
            if (requestBody == null)
                requestBody = IOUtils.toByteArray(getRequest().getInputStream());
            return requestBody;
        }

    }

}
