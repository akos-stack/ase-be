package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.exceptions.TokenException;
import com.bloxico.userservice.facade.IUserPasswordFacade;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

public class UserPasswordFacadeImplTest extends AbstractUnitTest {

    @Autowired
    private IUserPasswordFacade userPasswordFacade;

    @Autowired
    private PasswordTokenServiceImpl passwordTokenService;

    @Autowired
    private PasswordEncoder encoder;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenNewValidForgotPasswordInitRequest_whenResetForgottenPassword_thenCreatePasswordResetToken() {

        //given
        ForgotPasswordInitRequest initRequest = new ForgotPasswordInitRequest();
        initRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        userPasswordFacade.handleForgotPasswordRequest(initRequest);

        //then
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto tokenDb = passwordTokenService.getTokenByUserId(coinUser.getId());

        Assert.assertNotNull(tokenDb);

    }

    @Test
    @DirtiesContext
    public void givenExistingValidForgotPasswordInitRequest_whenResetForgottenPassword_thenReturnExistingResetToken() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto existingToken = passwordTokenService.createTokenForUser(coinUser.getId());

        //given
        ForgotPasswordInitRequest initRequest = new ForgotPasswordInitRequest();
        initRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        userPasswordFacade.handleForgotPasswordRequest(initRequest);

        //then
        TokenDto tokenDb = passwordTokenService.getTokenByUserId(coinUser.getId());
        Assert.assertEquals(tokenDb.getId(), existingToken.getId());
    }

    @Test
    @DirtiesContext
    public void givenInvalidForgotPasswordInitRequest_whenResetForgottenPassword_thenThrowCoinUserException() {

        //given
        ForgotPasswordInitRequest initRequest = new ForgotPasswordInitRequest();
        initRequest.setEmail("qweqwe@qweqweqwewqe.com");

        //when
        try {
            userPasswordFacade.handleForgotPasswordRequest(initRequest);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenValidForgotPasswordChangeRequest_whenUpdatingForgottenPassword_thenUpdateForgottenPassword() {

        CoinUser preUpdatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        createResetPasswordTokenForEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        TokenDto token = passwordTokenService.getTokenByUserId(preUpdatedCoinUser.getId());

        //given
        ForgotPasswordChangeRequest forgotPasswordChangeRequest = new ForgotPasswordChangeRequest();
        forgotPasswordChangeRequest.setTokenValue(token.getTokenValue());
        forgotPasswordChangeRequest.setNewPassword("novaSifra123!");
        forgotPasswordChangeRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);


        //when
        userPasswordFacade.updateForgottenPassword(forgotPasswordChangeRequest);

        //then
        CoinUser updatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(encoder.matches(forgotPasswordChangeRequest.getNewPassword(), updatedCoinUser.getPassword()));

    }

    @Test
    @DirtiesContext
    public void givenNotMatchingUserEmailWithToken_whenUpdatingForgottenPassword_thenThrow() {
        String newEmail = "newUser@qwqewweqe.com";
        mockUtil.insertMockUser(newEmail);

        CoinUser preUpdatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        createResetPasswordTokenForEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        TokenDto token = passwordTokenService.getTokenByUserId(preUpdatedCoinUser.getId());

        //given
        ForgotPasswordChangeRequest forgotPasswordChangeRequest = new ForgotPasswordChangeRequest();
        forgotPasswordChangeRequest.setTokenValue(token.getTokenValue());
        forgotPasswordChangeRequest.setNewPassword("novaSifra123!");
        forgotPasswordChangeRequest.setEmail(newEmail);


        //when
        try {
            userPasswordFacade.updateForgottenPassword(forgotPasswordChangeRequest);
            Assert.fail();
        } catch (TokenException e) {
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenInvalidTokenValue_whenUpdatingForgottenPassword_thenReturnTokenException() {

        //given
        ForgotPasswordChangeRequest forgotPasswordChangeRequest = new ForgotPasswordChangeRequest();
        forgotPasswordChangeRequest.setTokenValue("123");
        forgotPasswordChangeRequest.setNewPassword("novaSifra123!");
        forgotPasswordChangeRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        try {
            userPasswordFacade.updateForgottenPassword(forgotPasswordChangeRequest);
            Assert.fail();
        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenExpiredTokenValue_whenUpdatingForgottenPassword_thenReturnTokenException() {

        createResetPasswordTokenForEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        PasswordResetToken token = mockUtil.expirePasswordTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //given
        ForgotPasswordChangeRequest forgotPasswordChangeRequest = new ForgotPasswordChangeRequest();
        forgotPasswordChangeRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        forgotPasswordChangeRequest.setTokenValue(token.getTokenValue());
        forgotPasswordChangeRequest.setNewPassword("novaSifra123!");

        //when
        try {
            userPasswordFacade.updateForgottenPassword(forgotPasswordChangeRequest);
            Assert.fail();
        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_EXPIRED.getCode(), e.getMessage());
        }
    }


    @Test
    @DirtiesContext
    public void givenValidUserPasswordRequest_whenUpdatingKnownPassword_thenUpdateKnownPassword() {

        //given
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword(MockUtil.Constants.MOCK_USER_PASSWORD);
        updatePasswordRequest.setNewPassword("newPassword1!");

        //when
        userPasswordFacade.updateKnownPassword(MockUtil.Constants.MOCK_USER_EMAIL, updatePasswordRequest);

        //then
        CoinUser updatedUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(encoder.matches(updatePasswordRequest.getNewPassword(), updatedUser.getPassword()));
    }

    @Test
    @DirtiesContext
    public void givenInvalidOldPassword_whenUpdatingKnownPassword_thenReturnPasswordsDoNotMatchError() {

        //given
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("notCorrectPassword1!");
        updatePasswordRequest.setNewPassword("newPassword1!");

        //when
        try {
            userPasswordFacade.updateKnownPassword(MockUtil.Constants.MOCK_USER_EMAIL, updatePasswordRequest);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.OLD_PASSWORD_DOES_NOT_MATCH.getCode(), e.getMessage());
        }
    }

    private void createResetPasswordTokenForEmail(String email) {

        ForgotPasswordInitRequest initRequest = new ForgotPasswordInitRequest();
        initRequest.setEmail(email);

        userPasswordFacade.handleForgotPasswordRequest(initRequest);
    }
}
