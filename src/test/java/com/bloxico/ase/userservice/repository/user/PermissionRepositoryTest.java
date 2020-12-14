package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Permission;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class PermissionRepositoryTest extends AbstractSpringTest {

    @Autowired
    private PermissionRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById((short) -1).isEmpty());
        var permission = new Permission();
        permission.setName("foo");
        var id = repository.saveAndFlush(permission).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
