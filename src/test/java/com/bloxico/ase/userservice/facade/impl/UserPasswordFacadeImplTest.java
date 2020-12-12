package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.password.ForgottenPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.KnownPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserPasswordFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserPasswordFacadeImpl userPasswordFacade;

    @Test(expected = NullPointerException.class)
    public void handleForgotPasswordRequest_nullRequest() {
        userPasswordFacade.handleForgotPasswordRequest(null);
    }

    @Test(expected = UserProfileException.class)
    public void handleForgotPasswordRequest_userNotFound() {
        var email = uuid();
        var request = new ForgotPasswordRequest(email);
        userPasswordFacade.handleForgotPasswordRequest(request);
    }

    @Test
    public void handleForgotPasswordRequest() {
        var user = mockUtil.savedAdmin();
        assertTrue(tokenRepository.findByTypeAndUserId(PASSWORD_RESET, user.getId()).isEmpty());
        var email = user.getEmail();
        var request = new ForgotPasswordRequest(email);
        userPasswordFacade.handleForgotPasswordRequest(request);
        assertTrue(tokenRepository.findByTypeAndUserId(PASSWORD_RESET, user.getId()).isPresent());
        userPasswordFacade.handleForgotPasswordRequest(request);
        assertEquals(1, tokenRepository.findAll().size());
    }

    @Test(expected = NullPointerException.class)
    public void resendPasswordToken_nullRequest() {
        userPasswordFacade.resendPasswordToken(null);
    }

    @Test(expected = UserProfileException.class)
    public void resendPasswordToken_userNotFound() {
        var email = uuid();
        var request = new ResendTokenRequest(email);
        userPasswordFacade.resendPasswordToken(request);
    }

    @Test(expected = TokenException.class)
    public void resendPasswordToken_tokenNotFound() {
        var email = mockUtil.savedAdmin().getEmail();
        var request = new ResendTokenRequest(email);
        userPasswordFacade.resendPasswordToken(request);
    }

    @Test
    public void resendPasswordToken() {
        var email = mockUtil.savedAdmin().getEmail();
        mockUtil.doForgotPasswordRequest(email);
        var request = new ResendTokenRequest(email);
        userPasswordFacade.resendPasswordToken(request);
    }

    @Test(expected = NullPointerException.class)
    public void updateForgottenPassword_nullRequest() {
        userPasswordFacade.updateForgottenPassword(null);
    }

    @Test(expected = UserProfileException.class)
    public void updateForgottenPassword_userNotFound() {
        var token = mockUtil.doForgotPasswordRequest(mockUtil.savedAdmin().getEmail());
        var email = uuid();
        var password = "password";
        var request = new ForgottenPasswordUpdateRequest(email, token, password);
        userPasswordFacade.updateForgottenPassword(request);
    }

    @Test(expected = TokenException.class)
    public void updateForgottenPassword_tokenNotFound() {
        var email = mockUtil.savedAdmin().getEmail();
        var token = uuid();
        var password = "password";
        var request = new ForgottenPasswordUpdateRequest(email, token, password);
        userPasswordFacade.updateForgottenPassword(request);
    }

    @Test
    public void updateForgottenPassword() {
        var email = mockUtil.savedAdmin().getEmail();
        var token = mockUtil.doForgotPasswordRequest(email);
        var password = "updateForgottenPassword";
        var request = new ForgottenPasswordUpdateRequest(email, token, password);
        assertTrue(tokenRepository.findByValue(token).isPresent());
        userPasswordFacade.updateForgottenPassword(request);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(passwordEncoder.matches(
                password,
                userProfileService.findUserProfileByEmail(email).getPassword()));
    }

    @Test(expected = NullPointerException.class)
    public void updateKnownPassword_nullRequest() {
        userPasswordFacade.updateKnownPassword(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateKnownPassword_userNotFound() {
        var request = new KnownPasswordUpdateRequest("old", "new");
        userPasswordFacade.updateKnownPassword(-1, request);
    }

    @Test(expected = UserProfileException.class)
    public void updateKnownPassword_oldPasswordMismatch() {
        var oldPassword = uuid();
        var newPassword = "newPassword";
        var request = new KnownPasswordUpdateRequest(oldPassword, newPassword);
        var userId = mockUtil.savedAdmin().getId();
        userPasswordFacade.updateKnownPassword(userId, request);
    }

    @Test
    public void updateKnownPassword() {
        var oldPassword = "admin";
        var newPassword = "updateKnownPassword";
        var request = new KnownPasswordUpdateRequest(oldPassword, newPassword);
        var userId = mockUtil.savedAdmin().getId();
        userPasswordFacade.updateKnownPassword(userId, request);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userProfileService.findUserProfileById(userId).getPassword()));
    }

    @Test(expected = NullPointerException.class)
    public void setNewPassword_nullRequest() {
        userPasswordFacade.setNewPassword(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void setNewPassword_userNotFound() {
        var request = new SetPasswordRequest("new");
        userPasswordFacade.setNewPassword(-1, request);
    }

    @Test
    public void setNewPassword() {
        var password = "setNewPassword";
        var request = new SetPasswordRequest(password);
        var userId = mockUtil.savedAdmin().getId();
        userPasswordFacade.setNewPassword(userId, request);
        assertTrue(passwordEncoder.matches(
                password,
                userProfileService.findUserProfileById(userId).getPassword()));
    }

}
