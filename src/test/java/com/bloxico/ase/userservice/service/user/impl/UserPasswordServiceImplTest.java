package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class UserPasswordServiceImplTest extends AbstractSpringTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private UserPasswordServiceImpl userPasswordService;

    @Test(expected = NullPointerException.class)
    public void updateForgottenPassword_nullPassword() {
        userPasswordService.updateForgottenPassword(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateForgottenPassword_userNotFound() {
        userPasswordService.updateForgottenPassword(-1, "userNotFound@mail.com");
    }

    @Test
    public void updateForgottenPassword() {
        var user = mockUtil.savedAdmin();
        var newPassword = "updateForgottenPassword";
        userPasswordService.updateForgottenPassword(user.getId(), newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userProfileService.findUserProfileById(user.getId()).getPassword()));
    }

    @Test(expected = NullPointerException.class)
    public void updateKnownPassword_nullOldPassword() {
        userPasswordService.updateKnownPassword(1, null, "newPassword");
    }

    @Test(expected = NullPointerException.class)
    public void updateKnownPassword_nullNewPassword() {
        userPasswordService.updateKnownPassword(1, "oldPassword", null);
    }

    @Test(expected = NullPointerException.class)
    public void updateKnownPassword_nullBothPasswords() {
        userPasswordService.updateKnownPassword(1, null, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateKnownPassword_userNotFound() {
        userPasswordService.updateKnownPassword(-1, "oldPassword", "newPassword");
    }

    @Test(expected = UserProfileException.class)
    public void updateKnownPassword_oldPasswordMismatch() {
        var user = mockUtil.savedAdmin();
        var oldPassword = UUID.randomUUID().toString();
        var newPassword = "updateKnownPassword";
        userPasswordService.updateKnownPassword(user.getId(), oldPassword, newPassword);
    }

    @Test
    public void updateKnownPassword() {
        var user = mockUtil.savedAdmin();
        var oldPassword = "admin";
        var newPassword = "updateKnownPassword";
        userPasswordService.updateKnownPassword(user.getId(), oldPassword, newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userProfileService.findUserProfileById(user.getId()).getPassword()));
    }

    @Test(expected = NullPointerException.class)
    public void setNewPassword_nullPassword() {
        userPasswordService.setNewPassword(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void setNewPassword_userNotFound() {
        userPasswordService.setNewPassword(-1, "userNotFound@mail.com");
    }

    @Test
    public void setNewPassword() {
        var user = mockUtil.savedUserProfileDto();
        var newPassword = "setNewPassword";
        userPasswordService.setNewPassword(user.getId(), newPassword);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userProfileService.findUserProfileById(user.getId()).getPassword()));
    }

}
