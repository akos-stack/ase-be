package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoleRepositoryTest extends AbstractSpringTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void saveAndFindById() {
        assertTrue(roleRepository.findById((short) -1).isEmpty());
        var role = new Role();
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
        var id = roleRepository.saveAndFlush(role).getId();
        assertTrue(roleRepository.findById(id).isPresent());
    }

    @Test
    public void findByNameIgnoreCase() {
        assertTrue(roleRepository.findByNameIgnoreCase(uuid()).isEmpty());
        assertTrue(roleRepository.findByNameIgnoreCase("uSeR").isPresent());
    }

    @Test
    public void getUserRole() {
        assertEquals(Role.USER, roleRepository.getUserRole().getName());
    }

}
