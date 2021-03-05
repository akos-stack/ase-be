package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
import com.bloxico.ase.userservice.web.model.user.PagedUserDataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.web.api.UserManagementApi.*;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class UserManagementApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilUser utilUser;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilSecurityContext utilSecurityContext;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void searchUsers_403_forbidden() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
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
    @WithMockCustomUser(auth = true)
    public void searchUsers_404_roleNotExists() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("email", "user1")
                .param("role", genUUID())
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.ROLE_NOT_FOUND.getCode()));
    }

    @Test
    public void searchUsers_nothingFound_200_ok() {
        utilUser.savedUserDtoWithEmail(genEmail("barFoo"));
        utilUser.savedUserDtoWithEmail(genEmail("barFoo"));
        utilUser.savedUserDtoWithEmail(genEmail("barFoo"));
        var users = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("email", genEmail())
                .param("role", "admin")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedUserDataResponse.class)
                .getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchUsers_byEmail_200_ok() {
        var u1 = utilUser.savedUserDtoWithEmail(genEmail("fooBar"));
        var u2 = utilUser.savedUserDtoWithEmail(genEmail("fooBar"));
        var u3 = utilUser.savedUserDtoWithEmail(genEmail("barFoo"));
        var users = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("email", "fooBar")
                .param("role", "")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedUserDataResponse.class)
                .getUsers();
        assertThat(users, allOf(hasItems(u1, u2), not(hasItems(u3))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchUsers_byRole_200_ok() {
        var u1 = utilUser.savedUserDtoWithEmail(genEmail("barFoo"));
        var a1 = utilUser.savedAdminDtoWithEmail(genEmail("adminBarFoo"));
        var a2 = utilUser.savedAdminDtoWithEmail(genEmail("adminFooBar"));
        var users = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("email", "")
                .param("role", "admin")
                .param("size", Integer.MAX_VALUE)
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedUserDataResponse.class)
                .getUsers();
        assertThat(users, allOf(hasItems(a1, a2), not(hasItems(u1))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchUsers_byEmailAndRole_200_ok() {
        var admin = utilUser.savedAdminDtoWithEmail(genEmail("adFooBar"));
        var u1 = utilUser.savedUserDtoWithEmail(genEmail("fooBar"));
        var u2 = utilUser.savedUserDtoWithEmail(genEmail("fooBar"));
        var users = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("email", admin.getEmail())
                .param("role", "admin")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedUserDataResponse.class)
                .getUsers();
        assertThat(users, allOf(hasItems(admin), not(hasItems(u1, u2))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void disableUser_404_userNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
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
    @WithMockCustomUser(auth = true)
    public void disableUser_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var userToken = utilAuth.doAuthentication(registration);
        utilUserProfile.savedUserProfile(registration.getId());
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
                .header("Authorization", utilSecurityContext.getToken())
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
        utilAuth.doAuthenticationRequest(registration)
                .then()
                .assertThat()
                .statusCode(400)
                .body(
                        "error", is("invalid_grant"),
                        "error_description", is("User is disabled"));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void blacklistTokens_404_userNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
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
    @WithMockCustomUser(auth = true)
    public void blacklistTokens_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var userToken = utilAuth.doAuthentication(registration);
        utilUserProfile.savedUserProfile(registration.getId());
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
                .header("Authorization", utilSecurityContext.getToken())
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
        var newUserToken = utilAuth.doAuthentication(registration);
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
