package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_UPDATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UserProfileApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void accessMyProfile_200_ok() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "user_profile.id", notNullValue(),
                        "user_profile.locked", is(false),
                        "user_profile.enabled", is(true));
    }

    @Test
    public void updateMyProfile_200_ok() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new UpdateUserProfileRequest("updated_name", "updated_phone"))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "user_profile.name", is("updated_name"),
                        "user_profile.phone", is("updated_phone"));
    }

}
