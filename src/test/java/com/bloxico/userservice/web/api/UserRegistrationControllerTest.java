package com.bloxico.userservice.web.api;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.Role;
import com.bloxico.userservice.entities.user.UserRole;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.repository.user.RegionRepository;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.api.UserRegistrationApi;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.ApiError;
import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static io.restassured.RestAssured.given;


public class UserRegistrationControllerTest extends AbstractUnitTest {

    @Autowired
    private VerificationTokenServiceImpl verificationTokenService;

    @Autowired
    private RegionRepository regionRepository;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @Transactional
    @DirtiesContext
    public void givenNewUserDto_whenRegistrationProcess_thenCreateNewUserWithVerificationToken() {

        RegistrationRequest newUserRegRequest = mockUtil.createMockRegistrationRequest("newuser@qweqwe.com");

        given().
                contentType(ContentType.JSON).
                body(newUserRegRequest).
                when().
                post(API_URL + UserRegistrationApi.REGISTRATION_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser newUser = mockUtil.findUserByEmail("newuser@qweqwe.com");

        Assert.assertNotNull(newUser);

        Assert.assertFalse(newUser.isEnabled());

        TokenDto token = verificationTokenService.getTokenByUserId(newUser.getId());
        Assert.assertNotNull(token);

        Assert.assertTrue(newUser.getUserRoles().contains(new UserRole(newUser, new Role(Role.RoleName.USER))));
    }


    @Test
    @DirtiesContext
    public void givenExistingUserDto_whenRegistrationProcess_thenConflictResponse() {

        RegistrationRequest mockUserRegRequest = mockUtil.createMockRegistrationRequest(MockUtil.Constants.MOCK_USER_EMAIL);

        Response response = given().
                contentType(ContentType.JSON).
                body(mockUserRegRequest).
                when().
                post(API_URL + UserRegistrationApi.REGISTRATION_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.CONFLICT.value());

        ApiError apiError = extractApiErrorFromResponse(response);

        Assert.assertEquals(ErrorCodes.USER_EXISTS.getCode(), apiError.getErrorCode());
    }

    @Test
    @DirtiesContext
    public void givenValidToken_whenSubmittingValidityRequest_thenEnableUser() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        TokenDto token = verificationTokenService.getTokenByUserId(coinUser.getId());

        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue(token.getTokenValue());
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        given().
                contentType(ContentType.JSON).
                body(tokenValidityRequest).
                when().
                post(API_URL + UserRegistrationApi.REGISTRATION_CONFIRMATION_ENDPOINT).
                then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        //refresh
        coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Assert.assertTrue(coinUser.isEnabled());
    }

    @Test
    @DirtiesContext
    public void givenInvalidToken_whenSubmittingValidityRequest_thenNotFoundResponse() {

        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue("1223");
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Response response = given().
                contentType(ContentType.JSON).
                body(tokenValidityRequest).
                when().
                post(API_URL + UserRegistrationApi.REGISTRATION_CONFIRMATION_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.NOT_FOUND.value());

        ApiError apiError = extractApiErrorFromResponse(response);

        Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), apiError.getErrorCode());
    }

    @Test
    @DirtiesContext
    public void givenExpiredToken_whenSubmittingValidityRequest_thenTokenExpiredErrorMessage() {

        VerificationToken token = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        TokenValidityRequest tokenValidityRequest = new TokenValidityRequest();
        tokenValidityRequest.setTokenValue(token.getTokenValue());
        tokenValidityRequest.setEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Response response = given().
                contentType(ContentType.JSON).
                body(tokenValidityRequest).
                when().
                post(API_URL + UserRegistrationApi.REGISTRATION_CONFIRMATION_ENDPOINT);

        ApiError apiError = extractApiErrorFromResponse(response);

        Assert.assertEquals(ErrorCodes.TOKEN_EXPIRED.getCode(), apiError.getErrorCode());
    }

    @Test
    @DirtiesContext
    public void givenExpiredTokenId_whenRefreshingExpiredToken_thenUpdateToken() {

        VerificationToken expiredToken = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        given().
                param(UserRegistrationApi.TOKEN_PARAM, expiredToken.getTokenValue()).
                when().
                get(API_URL + UserRegistrationApi.REGISTRATION_TOKEN_REFRESH_ENDPOINT);

        TokenDto newToken = verificationTokenService.getTokenByUserId(expiredToken.getUserId());

        Assert.assertTrue(newToken.getExpiryDate().after(new Date()));

    }

    @Test
    @DirtiesContext
    public void whenFetchingRegistrationData_thenReturnRegistrationData() {

        Response response = given().
                when().
                get(API_URL + UserRegistrationApi.REGISTRATION_PAGE_DATA_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        ResponseBody responseBody = response.getBody();

        RegistrationDataResponse registrationDataResponse = gson.fromJson(responseBody.asString(), RegistrationDataResponse.class);
        Assert.assertFalse(registrationDataResponse.getRegions().isEmpty());
    }
}
