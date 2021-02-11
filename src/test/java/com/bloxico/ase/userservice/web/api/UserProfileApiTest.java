package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_UPDATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class UserProfileApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilUserProfile utilUserProfile;

    @Test
    public void accessMyProfile_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var userProfileDto1 = utilUserProfile.savedUserProfileDto(registration.getId());
        var userProfileDto2 = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UserProfileDataResponse.class)
                .getUserProfile();
        assertEquals(userProfileDto1, userProfileDto2);
    }

    @Test
    public void updateMyProfile_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedUserProfileDto(registration.getId());
        var firstName = genUUID();
        var lastName = genUUID();
        var phone = genUUID();
        var userProfileDto = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(firstName, lastName, phone))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UserProfileDataResponse.class)
                .getUserProfile();
        assertEquals(firstName, userProfileDto.getFirstName());
        assertEquals(lastName, userProfileDto.getLastName());
        assertEquals(phone, userProfileDto.getPhone());
    }

}
