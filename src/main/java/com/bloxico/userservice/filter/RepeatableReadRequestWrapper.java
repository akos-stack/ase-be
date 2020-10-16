package com.bloxico.userservice.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * {@link HttpServletRequest#getInputStream()} can be read only once.
 * This wrapper makes a copy of it each time read is attempted so we don't encounter exception.
 */
public class RepeatableReadRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getRequestBody());

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

    public RepeatableReadRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private byte[] getRequestBody() throws IOException {
        if (requestBody == null) {
            requestBody = IOUtils.toByteArray(getRequest().getInputStream());
        }
        return requestBody;
    }

}