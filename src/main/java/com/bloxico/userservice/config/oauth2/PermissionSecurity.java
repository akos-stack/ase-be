package com.bloxico.userservice.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permissionSecurity")
@Slf4j
public class PermissionSecurity {

    public boolean authorize(Authentication authentication, final String permission) {
        log.info("Checking authorization for permission {} - start", permission);

        val userRoles = authentication.getAuthorities();
        //TODO Dzoni - fetch all permissions for user roles and see if "permission" is found
        // (we can cache all permissions)


        return true;
    }
}
