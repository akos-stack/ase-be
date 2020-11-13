package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertSame;

public class RolePermissionServiceImplTest extends AbstractSpringTest {

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    public void permissionNameGrantedAuthoritiesMap_isCached() {
        assertSame(
                // Naive, add Thread.sleep inside tested method
                rolePermissionService.permissionNameGrantedAuthoritiesMap(),
                rolePermissionService.permissionNameGrantedAuthoritiesMap());
    }

}
