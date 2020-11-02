package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MockUtil {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile superUser() {
        return userProfileRepository
                .findById(1L)
                .orElseThrow(AssertionError::new);
    }

    public UserProfile savedUser() {
        Role role = new Role();
        {
            Permission p1 = new Permission();
            p1.setName("permission_1");
            p1.setCreator(1L);
            p1 = permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("permission_2");
            p2.setCreator(1L);
            p2 = permissionRepository.saveAndFlush(p2);
            role.setName("role_x");
            role.setPermissions(Set.of(p1, p2));
            roleRepository.save(role);
        }
        UserProfile user = new UserProfile();
        user.setName("foobar");
        user.setPassword("foobar");
        user.setEmail("foobar@mail.com");
        user.setRole(role);
        return userProfileRepository.saveAndFlush(user);
    }

    public static void copyBaseEntityData(BaseEntity from, BaseEntity to) {
        to.setCreator(from.getCreator());
        to.setUpdater(from.getUpdater());
        to.setCreated(from.getCreated());
        to.setUpdated(from.getUpdated());
    }

}
