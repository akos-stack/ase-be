package com.bloxico.userservice.web.api;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.ApiError;
import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;

public class UserProfileControllerTest extends AbstractUnitTest {

    private CoinUserDto coinUserDto;

    @Before
    public void init() {

        coinUserDto = mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void whenRequestingMyProfileData_thenGetMyProfileData() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        Response response = given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                when().
                get(API_URL + UserProfileApi.MY_PROFILE_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        ResponseBody responseBody = response.getBody();
        UserProfileDataResponse dataResponse = gson.fromJson(responseBody.asString(), UserProfileDataResponse.class);

        //then
        Assert.assertEquals(dataResponse.getUserProfile().getEmail(), MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertFalse(dataResponse.getRegions().isEmpty());

    }

    @Test
    @DirtiesContext
    public void givenValidUserProfileRequest_whenUpdatingMyData_thenUpdateMyData() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "Croatia";

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCity(newCity);
        updateProfileRequest.setName(newName);
        updateProfileRequest.setRegion(newRegion);


        Response response = given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                body(updateProfileRequest).
                when().
                post(API_URL + UserProfileApi.UPDATE_MY_PROFILE_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.OK.value());

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Assert.assertEquals(coinUser.getUserProfile().getName(), newName);
        Assert.assertEquals(coinUser.getUserProfile().getCity(), newCity);
        Assert.assertEquals(coinUser.getUserProfile().getRegion().getRegionName(), newRegion);
    }

    @Test
    @DirtiesContext
    public void givenInvalidRegionInRequest_whenUpdatingMyData_thenUpdateMyData() {

        String accessToken = mockUtil.enableAndAuthenticateUser(MockUtil.Constants.MOCK_USER_EMAIL, MockUtil.Constants.MOCK_USER_PASSWORD);

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "UnknownCity";

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCity(newCity);
        updateProfileRequest.setName(newName);
        updateProfileRequest.setRegion(newRegion);


        Response response = given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                body(updateProfileRequest).
                when().
                post(API_URL + UserProfileApi.UPDATE_MY_PROFILE_ENDPOINT);

        response.then().
                assertThat().
                statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ApiError apiError = extractApiErrorFromResponse(response);
        Assert.assertEquals(ErrorCodes.REGION_NOT_FOUND.getCode(), apiError.getErrorCode());
    }
}