package com.bloxico.userservice.services.oauth.impl;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.OAuthTokenServiceException;
import com.bloxico.userservice.services.user.IUserPasswordService;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class OauthTokenServiceImplTest extends AbstractUnitTest {

    @Autowired
    private IUserRegistrationService registrationService;

    @Autowired
    private IUserPasswordService passwordService;

    @Autowired
    private OauthTokenServiceImpl oauthTokenService;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
        mockUtil.insertMockUser("randUser@rand.com");
    }

    @Test
    @DirtiesContext
    public void whenAuthenticatingIntegratedUser_thenReturnValidAccessToken() {

        //given
        CoinUser coinUser1 = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        CoinUser coinUser2 = mockUtil.findUserByEmail("randUser@rand.com");

        registrationService.enableUser(coinUser1.getId());
        registrationService.enableUser(coinUser2.getId());

        //when
        String tokenFirst = oauthTokenService.authenticateIntegratedUser(MockUtil.Constants.MOCK_USER_EMAIL);
        String tokenSameAsFirst = oauthTokenService.authenticateIntegratedUser(MockUtil.Constants.MOCK_USER_EMAIL);

        String tokenSecond = oauthTokenService.authenticateIntegratedUser("randUser@rand.com");

        //then
        Assert.assertEquals(tokenFirst, tokenSameAsFirst);
        Assert.assertNotEquals(tokenFirst, tokenSecond);

    }

    @Test
    @DirtiesContext
    public void whenSigningInInternallyAuthenticatedUser_thenSignInProperly() {

        //given
        CoinUser coinUser1 = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        CoinUser coinUser2 = mockUtil.findUserByEmail("randUser@rand.com");

        registrationService.enableUser(coinUser1.getId());
        registrationService.enableUser(coinUser2.getId());

        //when
        String tokenFirst = oauthTokenService.authenticateIntegratedUser(MockUtil.Constants.MOCK_USER_EMAIL);
        String tokenSameAsFirst = oauthTokenService.authenticateIntegratedUser(MockUtil.Constants.MOCK_USER_EMAIL);

        String tokenSecond = oauthTokenService.authenticateIntegratedUser("randUser@rand.com");

        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setNewPassword("NovaSifra123!");
        passwordService.updateForgottenPassword(coinUser1.getId(), forgotPasswordDto);

        String tokenFromSignIn = mockUtil.authenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, "NovaSifra123!");

        //then
        Assert.assertEquals(tokenFirst, tokenSameAsFirst);
        Assert.assertEquals(tokenFirst, tokenFromSignIn);

        Assert.assertNotEquals(tokenFirst, tokenSecond);
    }

    @Test
    @DirtiesContext
    public void givenStandardTokenFromAuthenticatedUser_whenRetrievingAccessTokenFromIntegrationEndpoint_thenMatchAccessTokens() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setNewPassword("NovaSifra123!");
        passwordService.updateForgottenPassword(coinUser.getId(), forgotPasswordDto);

        registrationService.enableUser(coinUser.getId());

        String standardToken = mockUtil.authenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, forgotPasswordDto.getNewPassword());

        //when
        String implicitToken = oauthTokenService.authenticateIntegratedUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //then
        Assert.assertEquals(implicitToken, standardToken);
    }
}