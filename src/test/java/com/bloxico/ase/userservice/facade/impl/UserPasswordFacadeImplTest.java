package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserPasswordFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilAuth utilAuth;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserServiceImpl userService;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private UserPasswordFacadeImpl userPasswordFacade;
    @Autowired private UtilSecurityContext utilSecurityContext;

    @Test
    public void handleForgotPasswordRequest_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userPasswordFacade.handleForgotPasswordRequest(null));
    }

    @Test
    public void handleForgotPasswordRequest_userNotFound() {
        var request = new ForgotPasswordRequest(genEmail());
        assertThrows(
                UserException.class,
                () -> userPasswordFacade.handleForgotPasswordRequest(request));
    }

    @Test
    public void handleForgotPasswordRequest() {
        var user = utilUser.savedAdmin();
        assertTrue(tokenRepository.findByTypeAndUserId(PASSWORD_RESET, user.getId()).isEmpty());
        var request = new ForgotPasswordRequest(user.getEmail());
        userPasswordFacade.handleForgotPasswordRequest(request);
        assertTrue(tokenRepository.findByTypeAndUserId(PASSWORD_RESET, user.getId()).isPresent());
        userPasswordFacade.handleForgotPasswordRequest(request);
        assertTrue(tokenRepository
                .findAll().stream()
                .map(Token::getUserId)
                .anyMatch(user.getId()::equals));
    }

    @Test
    public void resendPasswordToken_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userPasswordFacade.resendPasswordToken(null));
    }

    @Test
    public void resendPasswordToken_userNotFound() {
        var request = new ResendTokenRequest(genEmail());
        assertThrows(
                UserException.class,
                () -> userPasswordFacade.resendPasswordToken(request));
    }

    @Test
    public void resendPasswordToken_tokenNotFound() {
        var email = utilUser.savedAdmin().getEmail();
        var request = new ResendTokenRequest(email);
        assertThrows(
                TokenException.class,
                () -> userPasswordFacade.resendPasswordToken(request));
    }

    @Test
    public void resendPasswordToken() {
        var email = utilUser.savedAdmin().getEmail();
        utilAuth.doForgotPasswordRequest(email);
        var request = new ResendTokenRequest(email);
        userPasswordFacade.resendPasswordToken(request);
    }

    @Test
    public void updateForgottenPassword_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userPasswordFacade.updateForgottenPassword(null));
    }

    @Test
    public void updateForgottenPassword_userNotFound() {
        var token = utilAuth.doForgotPasswordRequest(utilUser.savedAdmin().getEmail());
        var request = new ForgottenPasswordUpdateRequest(genEmail(), token, genPassword());
        assertThrows(
                UserException.class,
                () -> userPasswordFacade.updateForgottenPassword(request));
    }

    @Test
    public void updateForgottenPassword_tokenNotFound() {
        var email = utilUser.savedAdmin().getEmail();
        var request = new ForgottenPasswordUpdateRequest(email, genUUID(), genPassword());
        assertThrows(
                TokenException.class,
                () -> userPasswordFacade.updateForgottenPassword(request));
    }

    @Test
    public void updateForgottenPassword() {
        var email = utilUser.savedAdmin().getEmail();
        var token = utilAuth.doForgotPasswordRequest(email);
        var password = genPassword();
        var request = new ForgottenPasswordUpdateRequest(email, token, password);
        assertTrue(tokenRepository.findByValue(token).isPresent());
        userPasswordFacade.updateForgottenPassword(request);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(passwordEncoder.matches(
                password,
                userService.findUserByEmail(email).getPassword()));
    }

    @Test
    @WithMockCustomUser
    public void updateKnownPassword_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userPasswordFacade.updateKnownPassword(null));
    }

    @Test
    public void updateKnownPassword_userNotFound() {
        var request = new KnownPasswordUpdateRequest(genPassword(), genPassword());
        assertThrows(
                AssertionError.class,
                () -> userPasswordFacade.updateKnownPassword(request));
    }

    @Test
    @WithMockCustomUser
    public void updateKnownPassword_oldPasswordMismatch() {
        var request = new KnownPasswordUpdateRequest(genPassword(), genPassword());
        assertThrows(
                UserException.class,
                () -> userPasswordFacade.updateKnownPassword(request));
    }

    @Test
    @WithMockCustomUser
    public void updateKnownPassword() {
        var oldPassword = "pass";
        var newPassword = genPassword();
        var request = new KnownPasswordUpdateRequest(oldPassword, newPassword);
        userPasswordFacade.updateKnownPassword(request);
        assertTrue(passwordEncoder.matches(
                newPassword,
                userService.findUserById(utilSecurityContext.getLoggedInUserId()).getPassword()));
    }

    @Test
    @WithMockCustomUser
    public void setNewPassword_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userPasswordFacade.setNewPassword(null));
    }

    @Test // TODO ASE-236
    public void setNewPassword_userNotFound() {
        var request = new SetPasswordRequest(genPassword());
        assertThrows(
                AssertionError.class,
                () -> userPasswordFacade.setNewPassword(request));
    }

    @Test
    @WithMockCustomUser
    public void setNewPassword() {
        var password = genPassword();
        var request = new SetPasswordRequest(password);
        userPasswordFacade.setNewPassword(request);
        assertTrue(passwordEncoder.matches(
                password,
                userService.findUserById(utilSecurityContext.getLoggedInUserId()).getPassword()));
    }

}
