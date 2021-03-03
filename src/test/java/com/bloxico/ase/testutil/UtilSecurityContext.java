package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.config.security.AsePrincipal;
import com.bloxico.ase.userservice.entity.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UtilSecurityContext {

    public String getToken() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return (String) auth.getCredentials();
        }
        return "";
    }

    public Long getLoggedInUserId() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            AsePrincipal user = (AsePrincipal) auth.getPrincipal();
            return user.getUser().getId();
        }
        return null;
    }

    public User getLoggedInPrincipal() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            AsePrincipal user = (AsePrincipal) auth.getPrincipal();
            return user.getUser();
        }
        return null;
    }
}
