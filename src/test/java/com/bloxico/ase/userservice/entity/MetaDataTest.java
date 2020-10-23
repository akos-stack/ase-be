package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MetaDataTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void prePersist() {
        Permission permission = new Permission();
        permission.setName("FOO");
        assertNull(permission.getCreated());
        permission = permissionRepository.saveAndFlush(permission);
        assertNotNull(permission.getCreated());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void preUpdate_updater_isNull() {
        Permission oldPermission = new Permission();
        oldPermission.setName("FOO");
        oldPermission = permissionRepository.saveAndFlush(oldPermission);
        Permission newPermission = new Permission();
        newPermission.setId(oldPermission.getId());
        newPermission.setName("BAR"); // update
        MockUtil.copyMetaData(oldPermission, newPermission);
        permissionRepository.saveAndFlush(newPermission);
    }

    @Test
    public void preUpdate_updater_isNotNull() {
        Permission oldPermission = new Permission();
        oldPermission.setName("FOO");
        oldPermission = permissionRepository.saveAndFlush(oldPermission);
        Permission newPermission = new Permission();
        newPermission.setId(oldPermission.getId());
        newPermission.setName("BAR"); // update
        MockUtil.copyMetaData(oldPermission, newPermission);
        newPermission.setUpdater(mockUtil.savedUser().getId()); // !null
        assertNull(newPermission.getUpdated());
        newPermission = permissionRepository.saveAndFlush(newPermission);
        assertNotNull(newPermission.getUpdated());
    }

}
