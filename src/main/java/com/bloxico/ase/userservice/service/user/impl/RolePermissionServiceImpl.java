package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.service.user.IRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Slf4j
@Service
public class RolePermissionServiceImpl implements IRolePermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public RolePermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Cacheable("permissionNameGrantedAuthoritiesMapCache")
    public Map<String, Set<GrantedAuthority>> permissionNameGrantedAuthoritiesMap() {
        log.debug("RolePermissionServiceImpl.permissionNameGrantedAuthoritiesMap - start");
        Map<String, Set<GrantedAuthority>> map = permissionRepository
                .findAll()
                .stream()
                .collect(toUnmodifiableMap(
                        Permission::getName,
                        permission -> permission
                                .getRoles()
                                .stream()
                                .map(Role::toGrantedAuthority)
                                .collect(toUnmodifiableSet())));
        log.debug("RolePermissionServiceImpl.permissionNameGrantedAuthoritiesMap - end");
        return map;
    }

}
