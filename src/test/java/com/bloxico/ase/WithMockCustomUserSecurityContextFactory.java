package com.bloxico.ase;

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

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    UtilUser utilUser;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User principal = null;
        if(customUser.role() == Role.ADMIN) {
            principal = utilUser.savedAdmin();
        } else utilUser.savedUser();
        AsePrincipal asePrincipal = AsePrincipal.newUserDetails(principal);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(asePrincipal, "password", null);
        context.setAuthentication(auth);
        return context;
    }
}