package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.service.user.IRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component("permissionSecurity")
public class PermissionSecurity {

    @Autowired
    private IRolePermissionService rolePermissionService;

    public boolean isAuthorized(Authentication auth, String permission) {
        log.info("PermissionSecurity.isAuthorized - start | auth: {}, permission: {}", auth, permission);
        requireNonNull(auth);
        requireNonNull(permission);
        var authoritySet = rolePermissionService.permissionNameGrantedAuthoritiesMap().get(permission);
        var isAuthorized = auth
                .getAuthorities()
                .stream()
                .anyMatch(authoritySet::contains);
        log.info("PermissionSecurity.isAuthorized - end | auth: {}, permission: {}", auth, permission);
        return isAuthorized;
    }

}
