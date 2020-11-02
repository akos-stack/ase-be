package com.bloxico.ase.userservice.repository;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RoleRepositoryTest extends AbstractSpringTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void save() {
        Role role = new Role();
        {
            role.setName("foobar");
            Permission p1 = new Permission();
            p1.setName("foo");
            p1.setCreator(1L);
            permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("bar");
            p2.setCreator(1L);
            permissionRepository.saveAndFlush(p2);
            role.setPermissions(Set.of(p1, p2));
        }
        role = roleRepository.saveAndFlush(role);
        assertNotNull(role.getId());
    }

    @Test
    public void findById() {
        assertFalse(roleRepository.findById((short) -1).isPresent());
        Role role = new Role();
        {
            role.setName("foobar");
            Permission p1 = new Permission();
            p1.setName("foo");
            p1.setCreator(1L);
            permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("bar");
            p2.setCreator(1L);
            permissionRepository.saveAndFlush(p2);
            role.setPermissions(Set.of(p1, p2));
        }
        role = roleRepository.saveAndFlush(role);
        assertTrue(roleRepository.findById(role.getId()).isPresent());
    }

}
