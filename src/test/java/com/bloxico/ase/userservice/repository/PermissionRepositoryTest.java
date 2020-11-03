package com.bloxico.ase.userservice.repository;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PermissionRepositoryTest extends AbstractSpringTest {

    @Autowired
    private PermissionRepository repository;

    @Test
    public void save() {
        Permission permission = new Permission();
        permission.setName("foo");
        permission = repository.saveAndFlush(permission);
        assertNotNull(permission.getId());
    }

    @Test
    public void findById() {
        assertFalse(repository.findById((short) -1).isPresent());
        Permission permission = new Permission();
        permission.setName("foo");
        permission = repository.saveAndFlush(permission);
        assertTrue(repository.findById(permission.getId()).isPresent());
    }

}
