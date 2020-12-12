package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserRegistrationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

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
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(userProfileRepository.findByEmailIgnoreCase(email).orElseThrow().getEnabled());
    }

    @Test(expected = NullPointerException.class)
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
        var originalTokenDto = MAPPER.toTokenDto(
                tokenRepository
                        .findByValue(tokenValue)
                        .orElseThrow());
        userRegistrationFacade.refreshExpiredToken(tokenValue);
        var refreshedTokenDto = MAPPER.toTokenDto(
                tokenRepository
                        .findByTypeAndUserId(REGISTRATION, originalTokenDto.getUserId())
                        .orElseThrow());
        assertEquals(originalTokenDto.getId(), refreshedTokenDto.getId());
        assertEquals(originalTokenDto.getUserId(), refreshedTokenDto.getUserId());
        assertNotEquals(originalTokenDto.getValue(), refreshedTokenDto.getValue());
        assertTrue(originalTokenDto.getExpiryDate().isBefore(refreshedTokenDto.getExpiryDate()));
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
