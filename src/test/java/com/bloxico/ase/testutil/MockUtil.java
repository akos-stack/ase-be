package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.entity.MetaData;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
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

    public UserProfile savedUser() {
        Role role = new Role();
        {
            role.setName("ROLE_X");
            Permission p1 = new Permission();
            p1.setName("PERMISSION_1");
            p1 = permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("PERMISSION_2");
            p2 = permissionRepository.saveAndFlush(p2);
            role.setPermissions(Set.of(p1, p2));
            roleRepository.save(role);
        }
        UserProfile admin = new UserProfile();
        admin.setName("foobar");
        admin.setPassword("foobar");
        admin.setEmail("foobar@mail.com");
        admin.setRole(role);
        return userProfileRepository.saveAndFlush(admin);
    }

    public static void copyMetaData(MetaData from, MetaData to) {
        to.setCreator(from.getCreator());
        to.setUpdater(from.getUpdater());
        to.setCreated(from.getCreated());
        to.setUpdated(from.getUpdated());
    }

}
