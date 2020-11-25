package com.bloxico.ase.userservice.service.user;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

public interface IRolePermissionService {

    Map<String, Set<GrantedAuthority>> permissionNameGrantedAuthoritiesMap();

}
