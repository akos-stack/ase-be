package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.MockUtil.ERROR_CODE;
import static com.bloxico.ase.userservice.web.api.UserManagementApi.*;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UserManagementApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void searchUsers_notAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("email", "user1")
                .param("role", "")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void searchUsers_badRequest() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("email", "user1")
                .param("role", "nonExistingRole")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.ROLE_NOT_FOUND.getCode()));
    }

    @Test
    public void searchUsers_200_ok() {
        mockUtil.saveUsers();
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("email", "user1")
                .param("role", "")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body("users", notNullValue());
    }

    @Test
    public void disableUser_404_userNotFound() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new DisableUserRequest(-1L))
                .when()
                .post(API_URL + USER_DISABLE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void disableUser_200_ok() {
        var registration = mockUtil.doConfirmedRegistration();
        var userToken = mockUtil.doAuthentication(registration);
        mockUtil.savedUserProfile(registration.getId());
        // User can access secured endpoints
        given()
                .header("Authorization", userToken)
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
        // Disable user
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new DisableUserRequest(registration.getId()))
                .when()
                .post(API_URL + USER_DISABLE)
                .then()
                .assertThat()
                .statusCode(200);
        // User can't access secured endpoints with blacklisted token
        given()
                .header("Authorization", userToken)
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
        // User can't authenticate and obtain a new token
        mockUtil.doAuthenticationRequest(registration)
                .then()
                .assertThat()
                .statusCode(400)
                .body(
                        "error", is("invalid_grant"),
                        "error_description", is("User is disabled"));
    }

    @Test
    public void blacklistTokens_404_userNotFound() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new BlacklistTokensRequest(-1L))
                .when()
                .post(API_URL + USER_BLACKLIST_TOKENS)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void blacklistTokens_200_ok() {
        var registration = mockUtil.doConfirmedRegistration();
        var userToken = mockUtil.doAuthentication(registration);
        mockUtil.savedUserProfile(registration.getId());
        // User can access secured endpoints
        given()
                .header("Authorization", userToken)
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
        // Blacklist user's tokens
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new BlacklistTokensRequest(registration.getId()))
                .when()
                .post(API_URL + USER_BLACKLIST_TOKENS)
                .then()
                .assertThat()
                .statusCode(200);
        // User can't access secured endpoints with blacklisted token
        given()
                .header("Authorization", userToken)
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
        // User can authenticate and obtain a new token
        var newUserToken = mockUtil.doAuthentication(registration);
        // User can access secured endpoints with the new token
        given()
                .header("Authorization", newUserToken)
                .when()
                .get(API_URL + MY_PROFILE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
