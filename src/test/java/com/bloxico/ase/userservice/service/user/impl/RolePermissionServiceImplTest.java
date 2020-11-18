package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.config.AseUserDetails.authorityOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RolePermissionServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    public void permissionNameGrantedAuthoritiesMap() {
        var role = mockUtil.savedUserProfile().getRoles().iterator().next();
        assertFalse(role.getPermissions().isEmpty());
        var map = rolePermissionService.permissionNameGrantedAuthoritiesMap();
        assertFalse(map.isEmpty());
        role.getPermissions().forEach(permission -> assertTrue(
                map.get(permission
                        .getName())
                        .contains(authorityOf(role.getName()))));
        assertSame(map, rolePermissionService.permissionNameGrantedAuthoritiesMap());
    }

}
