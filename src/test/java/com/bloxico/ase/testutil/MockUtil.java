package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.util.mapper.EntityToDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MockUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile savedAdmin() {
        Role role = new Role();
        role.setName("admin");
        roleRepository.save(role);
        UserProfile user = new UserProfile();
        user.setName("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@mail.com");
        user.setRoles(Set.of(role));
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfile savedUserProfile() {
        Role role = new Role();
        {
            Permission p1 = new Permission();
            p1.setName("permission_1");
            p1 = permissionRepository.saveAndFlush(p1);
            Permission p2 = new Permission();
            p2.setName("permission_2");
            p2 = permissionRepository.saveAndFlush(p2);
            role.setName("role_x");
            role.setPermissions(Set.of(p1, p2));
            roleRepository.save(role);
        }
        UserProfile user = new UserProfile();
        user.setName("foobar");
        user.setPassword(passwordEncoder.encode("foobar"));
        user.setEmail("foobar@mail.com");
        user.setRoles(Set.of(role));
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfileDto savedUserProfileDto() {
        return EntityToDtoMapper.INSTANCE.userProfile(savedUserProfile());
    }

    public static void copyBaseEntityData(BaseEntity from, BaseEntity to) {
        to.setCreator(from.getCreator());
        to.setUpdater(from.getUpdater());
        to.setCreated(from.getCreated());
        to.setUpdated(from.getUpdated());
    }

}