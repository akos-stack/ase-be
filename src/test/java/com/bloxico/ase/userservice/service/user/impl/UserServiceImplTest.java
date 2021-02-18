package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import javassist.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.genPassword;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.UtilUser.genUserDto;
import static com.bloxico.ase.userservice.entity.user.Role.*;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepository;
    @Autowired private UserServiceImpl userService;

    @Test
    public void findUserById_notFound() {
        assertThrows(
                UserException.class,
                () -> userService.findUserById(-1));
    }

    @Test
    public void findUserById() {
        var userDto = utilUser.savedUserDto();
        assertEquals(
                userDto,
                userService.findUserById(userDto.getId()));
    }

    @Test
    public void findUserByEmail_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> userService.findUserByEmail(null));
    }

    @Test
    public void findUserByEmail_notFound() {
        assertThrows(
                UserException.class,
                () -> userService.findUserByEmail(genUUID()));
    }

    @Test
    public void findUserByEmail() {
        var userDto = utilUser.savedUserDto();
        assertEquals(
                userDto,
                userService.findUserByEmail(userDto.getEmail()));
    }

    // TODO-TEST findUsersByEmailOrRole_nullArgs
    @Test
    public void findUsersByEmailOrRole_nullArgs() {
        assertThrows(
                NullPointerException.class,
                () -> userService.findUsersByEmailOrRole(null, null, 0, 0, ""));
    }

    // TODO-TEST findUsersByEmailOrRole_emptyArgs
    @Test
    public void findUsersByEmailOrRole_emptyArgs() {
        assertThrows(
                NullPointerException.class,
                () -> userService.findUsersByEmailOrRole("", "", 0, 0, ""));
    }

    // TODO-TEST findUsersByEmailOrRole_notFound
    @Test
    public void findUsersByEmailOrRole_notFound() {
        assertThrows(
                NotFoundException.class,
                () -> userService.findUsersByEmailOrRole(genUUID(), "", 0, 0, ""));
    }

    @Test
    public void findUsersByEmailOrRole_byEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(u1.getEmail(), null, 0, MAX_VALUE, "name")
                        .getContent(),
                allOf(
                        hasItems(u1),
                        not(hasItems(u2, u3))));
    }

    @Test
    public void findUsersByEmailOrRole_byRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedAdminDto();
        var u3 = utilUser.savedAdminDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole("", ADMIN, 0, MAX_VALUE, "name")
                        .getContent(),
                allOf(
                        hasItems(u2, u3),
                        not(hasItems(u1))));
    }

    @Test
    public void findUsersByEmailOrRole_byRoleAndEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedAdminDto();
        var u3 = utilUser.savedAdminDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(u3.getEmail(), ADMIN, 0, MAX_VALUE, "name")
                        .getContent(),
                allOf(
                        hasItems(u3),
                        not(hasItems(u1, u2))));
    }

    @Test
    public void saveUser_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userService.saveUser(null));
    }

    @Test
    public void saveUser_userAlreadyExists() {
        var userDto = genUserDto();
        userService.saveUser(userDto);
        assertThrows(
                UserException.class,
                () -> userService.saveUser(userDto));
    }

    @Test
    public void saveUser_invalidAspirationName() {
        var userDto = genUserDto();
        userDto.setAspirationNames(Set.of(genUUID()));
        assertThrows(
                UserException.class,
                () -> userService.saveUser(userDto));
    }

    @Test
    public void saveUser_withoutAspirations() {
        var userDto = genUserDto();
        var user = userService.saveUser(userDto);
        assertNotNull(user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertNotEquals(userDto.getPassword(), user.getPassword());
    }

    @Test
    public void saveUser_withAspirations() {
        var userDto = genUserDto();
        userDto.setAspirationNames(Set.of(USER, EVALUATOR));
        var savedUserDto = userService.saveUser(userDto);
        assertNotNull(savedUserDto.getId());
        assertEquals(userDto.getEmail(), savedUserDto.getEmail());
        assertNotEquals(userDto.getPassword(), savedUserDto.getPassword());
        assertEquals(userDto.getAspirationNames(), savedUserDto.getAspirationNames());
    }

    @Test
    public void enableUser_notFound() {
        assertThrows(
                UserException.class,
                () -> userService.enableUser(-1, utilUser.savedAdmin().getId()));
    }

    @Test
    public void enableUser() {
        var principalId = utilUser.savedAdmin().getId();
        var regUser = userService.saveUser(genUserDto());
        assertFalse(regUser.getEnabled());
        userService.enableUser(regUser.getId(), principalId);
        var ebdUser = userRepository.findById(regUser.getId()).orElseThrow();
        assertTrue(ebdUser.getEnabled());
    }

    @Test
    public void disableUser_notFound() {
        var adminId = utilUser.savedAdmin().getId();
        assertThrows(
                UserException.class,
                () -> userService.disableUser(-1, adminId));
    }

    @Test
    public void disableUser() {
        var adminId = utilUser.savedAdmin().getId();
        var userId = utilUser.savedUser().getId();
        assertTrue(userService.findUserById(userId).getEnabled());
        userService.disableUser(userId, adminId);
        assertFalse(userService.findUserById(userId).getEnabled());
    }

    @Test
    public void deleteDisabledUsersWithIds_nullIds() {
        assertThrows(
                NullPointerException.class,
                () -> userService.deleteDisabledUsersWithIds(null));
    }

    @Test
    public void deleteDisabledUsersWithIds_emptyIds() {
        assertTrue(userService.deleteDisabledUsersWithIds(List.of()).isEmpty());
    }

    @Test
    public void deleteDisabledUsersWithIds() {
        var user1 = userService.saveUser(genUserDto());
        var user2 = userService.saveUser(genUserDto());
        var user3 = userService.saveUser(genUserDto());
        userService.enableUser(user1.getId(), utilUser.savedAdmin().getId());
        var regIds = Set.of(user1.getId(), user2.getId(), user3.getId());
        var delIds = Set.copyOf(userService.deleteDisabledUsersWithIds(regIds));
        assertEquals(Set.of(user2.getId(), user3.getId()), delIds);
    }

    @Test
    public void updatePassword_nullOldPassword() {
        assertThrows(
                NullPointerException.class,
                () -> userService.updatePassword(1, null, genPassword()));
    }

    @Test
    public void updatePassword_nullNewPassword() {
        assertThrows(
                NullPointerException.class,
                () -> userService.updatePassword(1, genPassword(), null));
    }

    @Test
    public void updatePassword_nullBothPasswords() {
        assertThrows(
                NullPointerException.class,
                () -> userService.updatePassword(1, null, null));
    }

    @Test
    public void updatePassword_userNotFound() {
        assertThrows(
                UserException.class,
                () -> userService.updatePassword(-1, genPassword(), genPassword()));
    }

    @Test
    public void updatePassword_oldPasswordMismatch() {
        var user = utilUser.savedAdmin();
        var oldPassword = genPassword();
        var newPassword = genPassword();
        assertThrows(
                UserException.class,
                () -> userService.updatePassword(user.getId(), oldPassword, newPassword));
    }

    @Test
    public void updatePassword() {
        var oldPassword = genPassword();
        var user = utilUser.savedAdmin(oldPassword);
        var newPassword = genPassword();
        userService.updatePassword(user.getId(), oldPassword, newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userService.findUserById(user.getId()).getPassword()));
    }

    @Test
    public void setNewPassword_nullPassword() {
        assertThrows(
                NullPointerException.class,
                () -> userService.setNewPassword(1, null));
    }

    @Test
    public void setNewPassword_userNotFound() {
        assertThrows(
                UserException.class,
                () -> userService.setNewPassword(-1, genPassword()));
    }

    @Test
    public void setNewPassword() {
        var user = utilUser.savedUser();
        var newPassword = genPassword();
        userService.setNewPassword(user.getId(), newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userService.findUserById(user.getId()).getPassword()));
    }

}
