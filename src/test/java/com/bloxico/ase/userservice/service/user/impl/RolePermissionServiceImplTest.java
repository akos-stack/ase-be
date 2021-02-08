package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.config.security.AsePrincipal.authorityOf;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RolePermissionServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    public void permissionNameGrantedAuthoritiesMap() {
        var role = mockUtil.savedUser().getRoles().iterator().next();
        assertFalse(role.getPermissions().isEmpty());
        var map = rolePermissionService.permissionNameGrantedAuthoritiesMap();
        assertFalse(map.isEmpty());
        role.getPermissions().forEach(permission ->
                assertThat(
                        map.get(permission.getName()),
                        hasItems(authorityOf(role.getName()))));
        assertSame(map, rolePermissionService.permissionNameGrantedAuthoritiesMap());
    }

    @Test
    public void findRoleByName_nullName() {
        assertThrows(
                NullPointerException.class,
                () -> rolePermissionService.findRoleByName(null));
    }

    @Test
    public void findRoleByName_notFound() {
        assertThrows(
                EntityNotFoundException.class,
                () -> rolePermissionService.findRoleByName(uuid()));
    }

    @Test
    public void findRoleByName() {
        assertNotNull(rolePermissionService.findRoleByName("uSeR"));
    }

}
