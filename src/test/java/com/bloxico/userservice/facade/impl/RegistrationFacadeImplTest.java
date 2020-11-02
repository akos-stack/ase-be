package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.CoinRole;
import com.bloxico.userservice.entities.user.CoinUserRole;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.exceptions.TokenException;
import com.bloxico.userservice.facade.IRegistrationFacade;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class RegistrationFacadeImplTest extends AbstractUnitTest {

    @Autowired
    private VerificationTokenServiceImpl verificationTokenService;
    @Autowired
    private IRegistrationFacade registrationFacade;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @DirtiesContext
    @Transactional
    @Test
    public void givenRegistrationRequest_whenRegisterUserWithVerificationToken_thenRegisterDisabledUserWithVerificationToken() {

        //given
        RegistrationRequest newUserRegRequest = mockUtil.createMockRegistrationRequest("newUser@noreply.com");

        //when
        registrationFacade.registerUserWithVerificationToken(newUserRegRequest);

        //then
        CoinUser newUser = mockUtil.findUserByEmail(newUserRegRequest.getEmail());
        Assert.assertNotNull(newUser);

        Assert.assertFalse(newUser.isEnabled());

        TokenDto token = verificationTokenService.getTokenByUserId(newUser.getId());
        Assert.assertNotNull(token);

        Assert.assertTrue(newUser.getCoinUserRoles().contains(new CoinUserRole(newUser, new CoinRole(CoinRole.RoleName.USER))));
    }

    @DirtiesContext
    @Test
    public void givenExistingUserRegistrationRequest_whenRegisterUserWithVerificationToken_thenThrowUserExistsError() {

        //given
        RegistrationRequest existingUserRequest = mockUtil.createMockRegistrationRequest(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        try {
            registrationFacade.registerUserWithVerificationToken(existingUserRequest);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_EXISTS.getCode(), e.getMessage());
        }
    }

    @DirtiesContext
    @Test
    public void givenValidValidityRequest_whenConfirmingToken_thenEnableUser() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //given
        TokenDto token = verificationTokenService.getTokenByUserId(coinUser.getId());

        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        tokenValidityRequest.setTokenValue(token.getTokenValue());

        //when
        registrationFacade.handleTokenValidation(tokenValidityRequest);

        //then
        CoinUser updatedUser = mockUtil.findUserByEmail(coinUser.getEmail());
        Assert.assertTrue(updatedUser.isEnabled());
    }

    @DirtiesContext
    @Test
    public void givenNonExistingTokenInValidityRequest_whenConfirmingToken_thenThrowTokenException() {

        //given
        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue("1234");
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        try {
            registrationFacade.handleTokenValidation(tokenValidityRequest);
            Assert.fail();

        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @DirtiesContext
    @Test
    public void givenNotMatchingTokenInValidityRequest_whenConfirmingToken_thenThrowTokenException() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        String newEmail = "newUser@email.com";
        mockUtil.insertMockUser("newUser@email.com");

        //given
        TokenDto token = verificationTokenService.getTokenByUserId(coinUser.getId());
        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue(token.getTokenValue());
        tokenValidityRequest.setEmail(newEmail);

        //when
        try {
            registrationFacade.handleTokenValidation(tokenValidityRequest);
            Assert.fail();
        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @DirtiesContext
    @Test
    public void givenExpiredTokenInValidityRequest_whenConfirmingToken_thenThrowTokenException() {
        //given
        VerificationToken expiredToken = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue(expiredToken.getTokenValue());
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        try {
            registrationFacade.handleTokenValidation(tokenValidityRequest);
            Assert.fail();

        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_EXPIRED.getCode(), e.getMessage());
        }
    }

    @DirtiesContext
    @Test
    public void givenExpiredTokenValue_whenRefreshingToken_thenRefreshToken() {
        //given
        VerificationToken expiredToken = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        registrationFacade.refreshExpiredToken(expiredToken.getTokenValue());

        //then
        TokenDto newToken = verificationTokenService.getTokenByUserId(expiredToken.getUserId());
        Assert.assertTrue(newToken.getExpiryDate().after(new Date()));
        Assert.assertEquals(expiredToken.getId(), newToken.getId());
    }

    @DirtiesContext
    @Test
    public void givenNonExistingTokenValue_whenRefreshingToken_thenSendTokenException() {

        //given
        String nonExistingTokenValue = "1234";

        //when
        try {
            registrationFacade.refreshExpiredToken(nonExistingTokenValue);
            Assert.fail();

        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @DirtiesContext
    @Test
    public void whenRetrievingRegistrationData_thenReturnRegistrationData() {

        //when
        RegistrationDataResponse registrationDataResponse = registrationFacade.returnRegistrationData();

        //then
        Assert.assertFalse(registrationDataResponse.getRegions().isEmpty());
    }
}
