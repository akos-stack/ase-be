package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import com.bloxico.userservice.exceptions.TokenException;
import com.bloxico.userservice.repository.token.VerificationTokenRepository;
import com.bloxico.userservice.util.mappers.EntityDataMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserRegistrationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRegistrationFacadeImpl userRegistrationFacade;

    @Test(expected = NullPointerException.class)
    public void registerUserWithVerificationToken_nullRequest() {
        userRegistrationFacade.registerUserWithVerificationToken(null);
    }

    @Test(expected = UserProfileException.class)
    public void registerUserWithVerificationToken_passwordMismatch() {
        var request = new RegistrationRequest("passwordMismatch@mail.com", "Password1!", "Password2!");
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test(expected = UserProfileException.class)
    public void registerUserWithVerificationToken_userAlreadyExists() {
        var request1 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        var request2 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(request1);
        userRegistrationFacade.registerUserWithVerificationToken(request2);
    }

    @Test
    public void registerDisabledUser() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test(expected = NullPointerException.class)
    public void handleTokenValidation_nullRequest() {
        userRegistrationFacade.handleTokenValidation(null);
    }

    @Test(expected = UserProfileException.class)
    public void handleTokenValidation_userNotFound() {
        var invalid = UUID.randomUUID().toString();
        var request = new TokenValidationRequest(invalid, invalid);
        userRegistrationFacade.handleTokenValidation(request);
    }

    @Test(expected = TokenException.class)
    public void handleTokenValidation_tokenNotFound() {
        var regRequest = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var invalid = UUID.randomUUID().toString();
        var tknRequest = new TokenValidationRequest(regRequest.getEmail(), invalid);
        userRegistrationFacade.handleTokenValidation(tknRequest);
    }

    @Test
    public void handleTokenValidation() {
        var email = "passwordMatches@mail.com";
        var regRequest = new RegistrationRequest(email, "Password1!", "Password1!");
        var token = userRegistrationFacade.registerUserWithVerificationToken(regRequest).getTokenValue();
        var tknRequest = new TokenValidationRequest(email, token);
        userRegistrationFacade.handleTokenValidation(tknRequest);
        assertTrue(tokenRepository.findByTokenValue(token).isEmpty());
        assertTrue(userProfileRepository.findByEmailIgnoreCase(email).orElseThrow().getEnabled());
    }

    @Test(expected = TokenException.class)
    public void refreshExpiredToken_nullRequest() {
        userRegistrationFacade.refreshExpiredToken(null);
    }

    @Test(expected = TokenException.class)
    public void refreshExpiredToken_tokenNotFound() {
        var token = UUID.randomUUID().toString();
        userRegistrationFacade.refreshExpiredToken(token);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void refreshExpiredToken() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var tokenValue = userRegistrationFacade.registerUserWithVerificationToken(request).getTokenValue();
        var originalTokenDto = EntityDataMapper.INSTANCE.tokenToDto(
                tokenRepository
                        .findByTokenValue(tokenValue)
                        .orElseThrow());
        userRegistrationFacade.refreshExpiredToken(tokenValue);
        var refreshedTokenDto = EntityDataMapper.INSTANCE.tokenToDto(
                tokenRepository
                        .findByUserId(originalTokenDto.getUserId())
                        .orElseThrow());
        assertEquals(originalTokenDto.getId(), refreshedTokenDto.getId());
        assertEquals(originalTokenDto.getUserId(), refreshedTokenDto.getUserId());
        assertNotEquals(originalTokenDto.getTokenValue(), refreshedTokenDto.getTokenValue());
        assertTrue(originalTokenDto.getExpiryDate().before(refreshedTokenDto.getExpiryDate()));
    }

    @Test(expected = NullPointerException.class)
    public void resendVerificationToken_nullRequest() {
        userRegistrationFacade.resendVerificationToken(null);
    }

    @Test(expected = UserProfileException.class)
    public void resendVerificationToken_userNotFound() {
        var request = new ResendTokenRequest(UUID.randomUUID().toString());
        userRegistrationFacade.resendVerificationToken(request);
    }

    @Test
    public void resendVerificationToken() {
        var email = "passwordMatches@mail.com";
        var regRequest = new RegistrationRequest(email, "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var resRequest = new ResendTokenRequest(email);
        userRegistrationFacade.resendVerificationToken(resRequest);
    }

}
