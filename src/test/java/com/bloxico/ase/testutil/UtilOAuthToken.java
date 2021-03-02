package com.bloxico.ase.testutil;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UtilOAuthToken {

    public String getToken() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return (String) auth.getCredentials();
        }
        return "";
    }
}
