package com.bloxico.ase.userservice.util;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.security.Principal;

import static java.util.Objects.requireNonNull;

public class Principals {

    private Principals() {
        throw new AssertionError();
    }

    public static long extractId(Principal principal) {
        requireNonNull(principal);
        var details = ((OAuth2Authentication) principal).getDetails();
        return details instanceof Long
                ? (Long) details
                : Long.valueOf((Integer) details);
    }

}
