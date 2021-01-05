/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version v0.1.1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bloxico.ase.userservice.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple request logging filter that writes the request URI
 * (and optionally the query string) to the Commons Log.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @see #setIncludeQueryString
 * @see #setBeforeMessagePrefix
 * @see #setBeforeMessageSuffix
 * @see #setAfterMessagePrefix
 * @see #setAfterMessageSuffix
 * @see org.apache.commons.logging.Log#debug(Object)
 * @since 1.v0.1.1.5
 */
public class CommonsRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return logger.isDebugEnabled();
    }

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        final String requestJson = maskPayload(request, message);
        logger.debug(requestJson);
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        final String requestJson = maskPayload(request, message);
        logger.debug(requestJson);
    }

    private String maskPayload(HttpServletRequest request, String message) {
        if (StringUtils.isEmpty(message)) return "";
        // don't display pass in log
        final String requestJson;
        if (request.getRequestURI().contains("sample/endpoint")) {
            // TODO put all endpoints and appropriate payloads that need to be masked here
            requestJson = maskPass("password", message);
        } else {
            requestJson = message;
        }
        return requestJson;
    }

    private String maskPass(final String passField, final String message) {
        String passFieldReplaceString = "\"" + passField + "\":\"*****\"";
        return message.replaceAll("\"" + passField + "\"\\s*?:\\s*?\".+?\"", passFieldReplaceString);
    }

}
