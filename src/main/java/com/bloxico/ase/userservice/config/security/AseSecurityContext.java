package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;

@Service
public class AseSecurityContext {

    @Autowired private ArtOwnerRepository artOwnerRepository;

    public static Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static long getPrincipalId() {
        var auth = auth();
        if (auth != null) {
            var oauth = (OAuth2Authentication) auth;
            var details = oauth.getDetails();
            return details instanceof Long
                    ? (Long) details
                    : Long.valueOf((Integer) details);
        }
        throw new AssertionError("Principal not found");
    }

    public long getArtOwnerId() {
        return artOwnerRepository
                .findByUserProfile_UserId(getPrincipalId())
                .map(ArtOwner::getId)
                .orElseThrow(USER_NOT_FOUND::newException);
    }

}
