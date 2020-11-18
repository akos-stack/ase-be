package com.bloxico.ase.userservice.util;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.security.Principal;

import static java.util.Objects.requireNonNull;

public class PrincipalUtil {

    private PrincipalUtil() {
        throw new AssertionError();
    }

    public static long extractId(Principal principal) {
        requireNonNull(principal);
        Long id = ((Long) ((OAuth2Authentication) principal).getDetails());
        requireNonNull(id);
        return id;
    }

}
