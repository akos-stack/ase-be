package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.testutil.UtilUser.genSearchUsersRequest;
import static com.bloxico.ase.testutil.UtilUser.genUserDto;
import static com.bloxico.ase.userservice.entity.user.Role.*;
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

    @Test
    public void findUsersByEmailOrRole_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userService.findUsersByEmailOrRole(null, allPages()));
    }

    @Test
    public void findUsersByEmailOrRole_nullPages() {
        assertThrows(
                NullPointerException.class,
                () -> userService.findUsersByEmailOrRole(genSearchUsersRequest(), null));
    }

    @Test
    public void findUsersByEmailOrRole_nullEmail() {
        assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userService.findUsersByEmailOrRole(genSearchUsersRequest(null), allPages()));
    }

    @Test
    public void findUsersByEmailOrRole_emptyEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest(""), allPages())
                        .getContent(),
                hasItems(u1, u2, u3));
    }

    @Test
    public void findUsersByEmailOrRole_nullRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest("", null), allPages())
                        .getContent(),
                hasItems(u1, u2, u3));
    }

    @Test
    public void findUsersByEmailOrRole_emptyRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest("", ""), allPages())
                        .getContent(),
                hasItems(u1, u2, u3));
    }

    @Test
    public void findUsersByEmailOrRole_blankRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest("", " "), allPages())
                        .getContent(),
                hasItems(u1, u2, u3));
    }

    @Test
    public void findUsersByEmailOrRole_invalidRole() {
        assertThrows(
                UserException.class,
                () -> userService.findUsersByEmailOrRole(genSearchUsersRequest("", genUUID()), allPages()));
    }

    @Test
    public void findUsersByEmailOrRole_notFound() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest(genUUID()), allPages())
                        .getContent(),
                not(hasItems(u1, u2, u3)));
    }

    @Test
    public void findUsersByEmailOrRole_foundByEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest(u1.getEmail()), allPages())
                        .getContent(),
                allOf(
                        hasItems(u1),
                        not(hasItems(u2, u3))));
    }

    @Test
    public void findUsersByEmailOrRole_foundByRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedAdminDto();
        var u3 = utilUser.savedAdminDto();
        assertThat(
                userService
                        .findUsersByEmailOrRole(genSearchUsersRequest("", ADMIN), allPages())
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
                        .findUsersByEmailOrRole(genSearchUsersRequest(u3.getEmail(), ADMIN), allPages())
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
    @WithMockCustomUser
    public void disableUser_notFound() {
        assertThrows(
                UserException.class,
                () -> userService.disableUser(-1));
    }

    @Test
    @WithMockCustomUser
    public void disableUser() {
        var userId = utilUser.savedUser().getId();
        assertTrue(userService.findUserById(userId).getEnabled());
        userService.disableUser(userId);
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
