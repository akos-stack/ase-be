package com.bloxico.userservice.web.api;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.api.UserPasswordApi;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.ApiError;
import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;


public class UserPasswordControllerTest extends AbstractUnitTest {

    @Autowired
    private VerificationTokenServiceImpl verificationTokenService;

    @Autowired
    private PasswordTokenServiceImpl passwordTokenService;

    @Autowired
    PasswordEncoder encoder;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenForgotPasswordInitRequest_whenForgotPassword_thenCreatePasswordResetToken() {

        ForgotPasswordInitRequest initRequest = new ForgotPasswordInitRequest();
        initRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        given().
                contentType(ContentType.JSON).
                body(initRequest).
                when().
                post(API_URL + UserPasswordApi.FORGOT_PASSWORD_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser coinUser = mockUtil.findUserByEmail(initRequest.getEmail());

        TokenDto passwordResetToken = passwordTokenService.getTokenByUserId(coinUser.getId());
        Assert.assertNotNull(passwordResetToken);
    }

    @Test
    @DirtiesContext
    public void givenForgotPasswordChangeRequest_whenForgotPassword_thenChangeForgottenPassword() {

        CoinUser preUpdatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto token = passwordTokenService.createTokenForUser(preUpdatedCoinUser.getId());

        ForgotPasswordChangeRequest changeRequest = new ForgotPasswordChangeRequest();
        changeRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        changeRequest.setNewPassword("novaSifra123!");
        changeRequest.setTokenValue(token.getTokenValue());

        given().
                contentType(ContentType.JSON).
                body(changeRequest).
                when().
                post(API_URL + UserPasswordApi.UPDATE_FORGOTTEN_PASSWORD_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser updatedCoinUser = mockUtil.findUserByEmail(preUpdatedCoinUser.getEmail());

        Assert.assertNotEquals(preUpdatedCoinUser.getPassword(), updatedCoinUser.getPassword());
    }

    @Test
    @DirtiesContext
    public void givenValidUserPasswordRequest_whenUpdatePassword_thenUpdatePassword() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        CoinUser preUpdatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("randomPassword1!");
        updatePasswordRequest.setNewPassword("novaSifra123!");

        given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                body(updatePasswordRequest).
                when().
                post(API_URL + UserPasswordApi.UPDATE_PASSWORD_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser updatedCoinUser = mockUtil.findUserByEmail(preUpdatedCoinUser.getEmail());

        Assert.assertTrue(encoder.matches(updatePasswordRequest.getNewPassword(), updatedCoinUser.getPassword()));
    }

    @Test
    @DirtiesContext
    public void givenInvalidUserPasswordRequest_whenUpdatePassword_thenSendNotMatchingPasswordResponse() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("notMatchingPassword1!");
        updatePasswordRequest.setNewPassword("novaSifra123!");

        Response response =

                given().
                        auth().oauth2(accessToken).
                        contentType(ContentType.JSON).
                        body(updatePasswordRequest).
                        when().
                        post(API_URL + UserPasswordApi.UPDATE_PASSWORD_ENDPOINT);

        ApiError apiError = extractApiErrorFromResponse(response);
        Assert.assertEquals(ErrorCodes.OLD_PASSWORD_DOES_NOT_MATCH.getCode(), apiError.getErrorCode());
    }

    @Test
    @DirtiesContext
    public void givenValidSetPasswordRequest_whenSettingNewPassword_thenSetNewPassword() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
        setPasswordRequest.setPassword("newPassword123!");

        given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                body(setPasswordRequest).
                when().
                post(API_URL + UserPasswordApi.SET_PASSWORD_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser updatedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Assert.assertTrue(encoder.matches(setPasswordRequest.getPassword(), updatedCoinUser.getPassword()));
    }

    @Test
    @DirtiesContext
    public void givenInvalidPassword_whenSettingNewPassword_thenThrowBadRequest() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
        setPasswordRequest.setPassword("invalidPassword");

        given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                body(setPasswordRequest).
                when().
                post(API_URL + UserPasswordApi.SET_PASSWORD_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
