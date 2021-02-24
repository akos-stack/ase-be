package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genEmail;
import static com.bloxico.ase.testutil.Util.genPassword;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.junit.Assert.assertTrue;

@Component
public class UtilUser {

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;

    public static UserDto genUserDto() {
        var email = genEmail();
        var userDto = new UserDto();
        userDto.setName(email);
        userDto.setPassword(genPassword());
        userDto.setEmail(email);
        return userDto;
    }

    public User savedAdmin() {
        return savedAdmin(genEmail());
    }

    public User savedAdmin(String password) {
        return savedAdmin(genEmail(), password);
    }

    public User savedAdmin(String email, String password) {
        var user = new User();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(roleRepository.getAdminRole());
        return userRepository.saveAndFlush(user);
    }

    public UserDto savedAdminDto() {
        return MAPPER.toDto(savedAdmin());
    }

    public User savedUser() {
        return savedUserWithEmail(genEmail());
    }

    public User savedUserWithEmail(String email) {
        return savedUser(email, genPassword());
    }

    public User savedUserWithPassword(String password) {
        return savedUser(genEmail(), password);
    }

    public User savedUser(String email, String password) {
        var user = new User();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        var role = roleRepository.getUserRole();
        user.addRole(role);
        user.addAspiration(role);
        return userRepository.saveAndFlush(user);
    }

    public UserDto savedUserDto() {
        return MAPPER.toDto(savedUser());
    }

    public UserDto savedUserDtoWithEmail(String email) {
        return MAPPER.toDto(savedUserWithEmail(email));
    }

    public void disableUser(Long userId) {
        var user = userRepository
                .findById(userId)
                .orElseThrow();
        assertTrue(user.getEnabled());
        user.setEnabled(false);
        user.setUpdaterId(user.getId());
        userRepository.saveAndFlush(user);
    }

    public void addRoleToUserWithId(String roleName, long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var role = roleRepository.getRole(roleName);
        user.addRole(role);
        user.setUpdaterId(userId);
        userRepository.saveAndFlush(user);
    }

}
