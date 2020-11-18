package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
            permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("bar");
            permissionRepository.saveAndFlush(p2);
            role.setPermissions(Set.of(p1, p2));
        }
        role = roleRepository.saveAndFlush(role);
        assertNotNull(role.getId());
    }

    @Test
    public void findById() {
        assertTrue(roleRepository.findById((short) -1).isEmpty());
        Role role = new Role();
        {
            role.setName("foobar");
            Permission p1 = new Permission();
            p1.setName("foo");
            permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("bar");
            permissionRepository.saveAndFlush(p2);
            role.setPermissions(Set.of(p1, p2));
        }
        role = roleRepository.saveAndFlush(role);
        assertTrue(roleRepository.findById(role.getId()).isPresent());
    }

    @Test
    public void findByNameIgnoreCase() {
        assertTrue(roleRepository.findByNameIgnoreCase(UUID.randomUUID().toString()).isEmpty());
        assertTrue(roleRepository.findByNameIgnoreCase("uSeR").isPresent());
    }

    @Test
    public void getUserRole() {
        assertEquals(Role.USER, roleRepository.getUserRole().getName());
    }

}
