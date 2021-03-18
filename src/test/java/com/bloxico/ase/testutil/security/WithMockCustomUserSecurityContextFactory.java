package com.bloxico.ase.testutil.security;

import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.testutil.UtilUserProfile;
import com.bloxico.ase.userservice.config.security.AseSecurityService;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
final class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    private final UtilUser utilUser;
    private final UtilAuth utilAuth;
    private final UtilUserProfile utilUserProfile;
    private final AseSecurityService aseSecurityService;

    @Autowired
    public WithMockCustomUserSecurityContextFactory(UtilUser utilUser, UtilAuth utilAuth, UtilUserProfile utilUserProfile, AseSecurityService aseSecurityService) {
        this.utilUser = utilUser;
        this.utilAuth = utilAuth;
        this.utilUserProfile = utilUserProfile;
        this.aseSecurityService = aseSecurityService;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // create user
        User principal = createPrincipal(customUser);

        UserDetails userDetails = aseSecurityService.loadUserByUsername(principal.getEmail());

        // authenticate
        String authenticationToken = "";
        if(customUser.auth()) {
            authenticationToken = utilAuth.doAuthentication(principal.getEmail(), customUser.password());
        }

        Authentication auth =
                new UsernamePasswordAuthenticationToken(userDetails, authenticationToken, null);
        context.setAuthentication(auth);

        List<String> scope = new ArrayList<>();
        scope.add("access_profile");

        OAuth2Request authRequest = new OAuth2Request(null, "appId", null, true, Sets.newHashSet(scope), null, null, null,
                null);
        OAuth2Authentication oAuth = new OAuth2Authentication(authRequest, auth);
        oAuth.setDetails(principal.getId());

        // Add the OAuth2Authentication object to the security context
        context.setAuthentication(oAuth);
        return context;
    }

    private User createPrincipal(WithMockCustomUser customUser) {
        User principal;
        if(Role.ADMIN.equals(customUser.role())) {
            if(customUser.email().isEmpty()) {
                principal = utilUser.savedAdmin(customUser.password());
            } else {
                principal = utilUser.savedAdmin(customUser.email(), customUser.password());
            }
        } else {
            if(customUser.email().isEmpty()) {
                principal = utilUser.savedUserWithPassword(customUser.password());
            } else {
                principal = utilUser.savedUser(customUser.email(), customUser.password());
            }

            if(Role.ART_OWNER.equals(customUser.role())) {
                utilUserProfile.savedArtOwnerDto(principal.getId());
            }

            if(Role.EVALUATOR.equals(customUser.role())) {
                utilUserProfile.savedEvaluatorDto(principal.getId());
            }
        }
        return principal;
    }
}