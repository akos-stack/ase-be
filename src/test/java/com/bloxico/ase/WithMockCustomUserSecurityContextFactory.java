package com.bloxico.ase;

import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.config.security.AsePrincipal;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    private UtilUser utilUser;

    @Autowired
    private UtilAuth utilAuth;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // create user
        User principal = createPrincipal(customUser);

        // authenticate
        String authenticationToken = utilAuth.doAuthentication(principal.getEmail(), customUser.password());

        AsePrincipal asePrincipal = AsePrincipal.newUserDetails(principal);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(asePrincipal, authenticationToken, null);
        context.setAuthentication(auth);
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
        }
        return principal;
    }
}