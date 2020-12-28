package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

public interface IRolePermissionService {

    Map<String, Set<GrantedAuthority>> permissionNameGrantedAuthoritiesMap();

    RoleDto findRoleByName(String name);

}
