package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import com.bloxico.ase.userservice.entity.user.profile.UserProfile;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepository;
import com.bloxico.ase.userservice.repository.user.profile.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.ACCESS_NOT_ALLOWED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;

@Slf4j
@Service
public class AseSecurityContextService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private ArtOwnerRepository artOwnerRepository;

    public Long getPrincipalId() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            var details = auth.getDetails();
            return details instanceof Long
                    ? (Long) details
                    : Long.valueOf((Integer) details);
        }
        return null;
    }

    public User getPrincipal() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            var principal = auth.getPrincipal();
            if(principal instanceof AsePrincipal) {
                var aseUser= (AsePrincipal) principal;
                return aseUser.getUser();
            } else {
                var aseUser = (String) principal;
                return userRepository.findByEmailIgnoreCase(aseUser).orElseThrow(() -> new UsernameNotFoundException(aseUser));
            }

        }
        return null;
    }

    public UserProfile getUserProfile() {
        var principalId = getPrincipalId();
        if(principalId != null) {
            return userProfileRepository.findByUserId(principalId).orElseThrow(USER_NOT_FOUND::newException);
        }
        throw USER_NOT_FOUND.newException();
    }

    public ArtOwner getArtOwner() {
        var principalId = getPrincipalId();
        if(principalId != null) {
            return artOwnerRepository.findByUserProfile_UserId(principalId).orElseThrow(USER_NOT_FOUND::newException);
        }
        throw USER_NOT_FOUND.newException();
    }

    public void validateOwner(Long ownerId) {
        var artOwner = artOwnerRepository.findById(ownerId);
        if (getPrincipal().getRoles().stream().noneMatch(role -> Role.ADMIN.equals(role.getName()))
                && artOwner.isPresent() && !artOwner.get().getUserProfile().getUserId().equals(getPrincipalId())) {
            throw ACCESS_NOT_ALLOWED.newException();
        }
    }

    public boolean isAdmin() {
        return getPrincipal().getRoles().stream().anyMatch(role -> Role.ADMIN.equals(role.getName()));
    }
}
