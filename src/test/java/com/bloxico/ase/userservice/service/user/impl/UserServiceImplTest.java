package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.user.Role.ADMIN;
import static org.junit.Assert.*;

public class UserServiceImplTest extends AbstractSpringTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Test(expected = UserException.class)
    public void findUserById_notFound() {
        userService.findUserById(-1);
    }

    @Test
    public void findUserById_found() {
        var userDto = mockUtil.savedUserDto();
        assertEquals(
                userDto,
                userService.findUserById(userDto.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void findUserByEmail_nullEmail() {
        userService.findUserByEmail(null);
    }

    @Test(expected = UserException.class)
    public void findUserByEmail_notFound() {
        userService.findUserByEmail(uuid());
    }

    @Test
    public void findUserByEmail_found() {
        var userDto = mockUtil.savedUserDto();
        assertEquals(
                userDto,
                userService.findUserByEmail(userDto.getEmail()));
    }

    @Test
    public void findUsersByEmailOrRole_byEmail() {
        mockUtil.saveUsers();
        assertEquals(1, userService.findUsersByEmailOrRole("user1", null, 0, 100, "name").size());
    }

    @Test
    public void findUsersByEmailOrRole_byRole() {
        mockUtil.saveUsers();
        assertEquals(4, userService.findUsersByEmailOrRole("", ADMIN, 0, 100, "name").size());
    }

    @Test
    public void findUsersByEmailOrRole_byRoleAndEmail() {
        mockUtil.saveUsers();
        assertEquals(3, userService.findUsersByEmailOrRole("user", ADMIN, 0, 100, "name").size());
    }

    @Test(expected = NullPointerException.class)
    public void saveDisabledUser_nullRequest() {
        userService.saveDisabledUser(null);
    }

    @Test(expected = UserException.class)
    public void saveDisabledUser_userAlreadyExists() {
        var userDto = mockUtil.genUserDto();
        userService.saveDisabledUser(userDto);
        userService.saveDisabledUser(userDto);
    }

    @Test
    public void saveDisabledUser() {
        var userDto = mockUtil.genUserDto();
        var user = userService.saveDisabledUser(userDto);
        assertNotNull(user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertTrue(userDto.getEmail().contains(user.getName()));
        assertNotEquals(userDto.getPassword(), user.getPassword());
        assertTrue(user.streamRoleNames().anyMatch(Role.USER::equals));
    }

    @Test(expected = NullPointerException.class)
    public void saveEnabledUser_nullUser() {
        userService.saveEnabledUser(null);
    }

    @Test(expected = UserException.class)
    public void saveEnabledUser_userAlreadyExists() {
        var userDto = mockUtil.genUserDto();
        userService.saveEnabledUser(userDto);
        userService.saveEnabledUser(userDto);
    }

    @Test
    public void saveEnabledUser() {
        var userDto = mockUtil.genUserDto();
        userDto = userService.saveEnabledUser(userDto);
        assertTrue(userDto.getEnabled());
        assertEquals(Set.of(), userDto.getRoles());
    }

    @Test(expected = UserException.class)
    public void enableUser_notFound() {
        userService.enableUser(-1);
    }

    @Test
    public void enableUser() {
        var regUser = userService.saveDisabledUser(mockUtil.genUserDto());
        assertFalse(regUser.getEnabled());
        userService.enableUser(regUser.getId());
        var ebdUser = userRepository.findById(regUser.getId()).orElseThrow();
        assertTrue(ebdUser.getEnabled());
        assertEquals(regUser.getId(), ebdUser.getUpdaterId());
    }

    @Test(expected = UserException.class)
    public void disableUser_notFound() {
        var adminId = mockUtil.savedAdmin().getId();
        userService.disableUser(-1, adminId);
    }

    @Test
    public void disableUser() {
        var adminId = mockUtil.savedAdmin().getId();
        var userId = mockUtil.savedUser().getId();
        assertTrue(userService.findUserById(userId).getEnabled());
        userService.disableUser(userId, adminId);
        assertFalse(userService.findUserById(userId).getEnabled());
    }

    @Test(expected = NullPointerException.class)
    public void deleteDisabledUsersWithIds_nullIds() {
        userService.deleteDisabledUsersWithIds(null);
    }

    @Test
    public void deleteDisabledUsersWithIds_emptyIds() {
        assertTrue(userService.deleteDisabledUsersWithIds(List.of()).isEmpty());
    }

    @Test
    public void deleteDisabledUsersWithIds() {
        var user1 = userService.saveDisabledUser(mockUtil.genUserDto());
        var user2 = userService.saveDisabledUser(mockUtil.genUserDto());
        var user3 = userService.saveDisabledUser(mockUtil.genUserDto());
        userService.enableUser(user1.getId());
        var regIds = Set.of(user1.getId(), user2.getId(), user3.getId());
        var delIds = Set.copyOf(userService.deleteDisabledUsersWithIds(regIds));
        assertEquals(Set.of(user2.getId(), user3.getId()), delIds);
    }

    @Test(expected = NullPointerException.class)
    public void updatePassword_nullOldPassword() {
        userService.updatePassword(1, null, "newPassword");
    }

    @Test(expected = NullPointerException.class)
    public void updatePassword_nullNewPassword() {
        userService.updatePassword(1, "oldPassword", null);
    }

    @Test(expected = NullPointerException.class)
    public void updatePassword_nullBothPasswords() {
        userService.updatePassword(1, null, null);
    }

    @Test(expected = UserException.class)
    public void updatePassword_userNotFound() {
        userService.updatePassword(-1, "oldPassword", "newPassword");
    }

    @Test(expected = UserException.class)
    public void updatePassword_oldPasswordMismatch() {
        var user = mockUtil.savedAdmin();
        var oldPassword = uuid();
        var newPassword = "updatePassword";
        userService.updatePassword(user.getId(), oldPassword, newPassword);
    }

    @Test
    public void updatePassword() {
        var oldPassword = "admin";
        var user = mockUtil.savedAdmin(oldPassword);
        var newPassword = "updatePassword";
        userService.updatePassword(user.getId(), oldPassword, newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userService.findUserById(user.getId()).getPassword()));
    }

    @Test(expected = NullPointerException.class)
    public void setNewPassword_nullPassword() {
        userService.setNewPassword(1, null);
    }

    @Test(expected = UserException.class)
    public void setNewPassword_userNotFound() {
        userService.setNewPassword(-1, "userNotFound@mail.com");
    }

    @Test
    public void setNewPassword() {
        var user = mockUtil.savedUser();
        var newPassword = "setNewPassword";
        userService.setNewPassword(user.getId(), newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userService.findUserById(user.getId()).getPassword()));
    }

}