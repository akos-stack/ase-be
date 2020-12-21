package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.service.user.IRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.bloxico.ase.userservice.config.security.AsePrincipal.authorityOf;
import static com.bloxico.ase.userservice.config.CacheConfig.PERMISSION_NAME_GRANTED_AUTHORITIES_MAP_CACHE;
import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Slf4j
@Service
public class RolePermissionServiceImpl implements IRolePermissionService {

    private final RoleRepository roleRepository;

    @Autowired
    public RolePermissionServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Cacheable(PERMISSION_NAME_GRANTED_AUTHORITIES_MAP_CACHE)
    public Map<String, Set<GrantedAuthority>> permissionNameGrantedAuthoritiesMap() {
        log.debug("RolePermissionServiceImpl.permissionNameGrantedAuthoritiesMap - start");
        var map = roleRepository
                .findAll()
                .stream()
                .flatMap(role -> role
                        .getPermissions()
                        .stream()
                        .map(permission -> entry(permission.getName(), authorityOf(role.getName()))))
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toUnmodifiableSet())));
        log.debug("RolePermissionServiceImpl.permissionNameGrantedAuthoritiesMap - end");
        return Map.copyOf(map); // immutable map
    }

}
