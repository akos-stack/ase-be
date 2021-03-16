package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.config.security.AsePrincipal;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class UtilSecurityContext {

    @Autowired
    private ArtOwnerRepository artOwnerRepository;

    public String getToken() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) auth.getUserAuthentication();
            return (String) authentication.getCredentials();
        }
        return "";
    }

    public Long getLoggedInUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            var details = auth.getDetails();
            return details instanceof Long
                    ? (Long) details
                    : Long.valueOf((Integer) details);
        }
        return null;
    }

    public User getLoggedInPrincipal() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) auth.getUserAuthentication();
            AsePrincipal user = (AsePrincipal) authentication.getPrincipal();
            return user.getUser();
        }
        return null;
    }

    public ArtOwner getLoggedInArtOwner() {
        return artOwnerRepository
                .findByUserProfile_UserId(getLoggedInUserId())
                .orElse(null);
    }

}
