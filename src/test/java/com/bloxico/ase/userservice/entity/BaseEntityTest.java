package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BaseEntityTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test(expected = NullPointerException.class)
    public void prePersist_creator_isNull() {
        Permission permission = new Permission();
        permission.setName("FOO");
        permissionRepository.saveAndFlush(permission);
    }

    @Test
    public void prePersist_creator_isNotNull() {
        Permission permission = new Permission();
        permission.setName("FOO");
        permission.setCreator(1L);
        assertNull(permission.getCreated());
        permission = permissionRepository.saveAndFlush(permission);
        assertNotNull(permission.getCreated());
    }

    @Test(expected = NullPointerException.class)
    public void preUpdate_updater_isNull() {
        Permission oldPermission = new Permission();
        oldPermission.setName("FOO");
        oldPermission.setCreator(1L);
        oldPermission = permissionRepository.saveAndFlush(oldPermission);
        Permission newPermission = new Permission();
        newPermission.setId(oldPermission.getId());
        newPermission.setName("BAR"); // update
        MockUtil.copyBaseEntityData(oldPermission, newPermission);
        permissionRepository.saveAndFlush(newPermission);
    }

    @Test
    public void preUpdate_updater_isNotNull() {
        Permission oldPermission = new Permission();
        oldPermission.setName("FOO");
        oldPermission.setCreator(1L);
        oldPermission = permissionRepository.saveAndFlush(oldPermission);
        Permission newPermission = new Permission();
        newPermission.setId(oldPermission.getId());
        newPermission.setName("BAR"); // update
        MockUtil.copyBaseEntityData(oldPermission, newPermission);
        newPermission.setUpdater(mockUtil.savedUserProfile().getId()); // !null
        assertNull(newPermission.getUpdated());
        newPermission = permissionRepository.saveAndFlush(newPermission);
        assertNotNull(newPermission.getUpdated());
    }

}
