package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
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
    public void saveDisabledUser_passwordMismatch() {
        var request = new RegistrationRequest("passwordMismatch@mail.com", "Password1!", "Password2!");
        userService.saveDisabledUser(request);
    }

    @Test(expected = UserException.class)
    public void saveDisabledUser_userAlreadyExists() {
        var request1 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        var request2 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        userService.saveDisabledUser(request1);
        userService.saveDisabledUser(request2);
    }

    @Test
    public void saveDisabledUser() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var user = userService.saveDisabledUser(request);
        assertNotNull(user.getId());
        assertEquals(request.getEmail(), user.getEmail());
        assertTrue(request.getEmail().contains(user.getName()));
        assertNotEquals(request.getPassword(), user.getPassword());
        assertTrue(user.streamRoleNames().anyMatch(Role.USER::equals));
    }

    @Test(expected = NullPointerException.class)
    public void saveEnabledUser_nullUser() {
        userService.saveEnabledUser(null);
    }

    @Test
    public void saveEnabledUser() {
        var userDto = new UserDto();
        userDto.setName(uuid());
        userDto.setEmail(uuid());
        userDto.setPassword(uuid());
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
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var regUser = userService.saveDisabledUser(request);
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
        var req1 = new RegistrationRequest("temp1@mail.com", "Password1!", "Password1!");
        var req2 = new RegistrationRequest("temp2@mail.com", "Password1!", "Password1!");
        var req3 = new RegistrationRequest("temp3@mail.com", "Password1!", "Password1!");
        var user1 = userService.saveDisabledUser(req1);
        var user2 = userService.saveDisabledUser(req2);
        var user3 = userService.saveDisabledUser(req3);
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
