package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_UPDATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class JwtAuthorizationFilterTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private ITokenBlacklistService blacklistService;

    @Test
    public void doFilterInternal_403_invalidAccessToken() {
        given()
                .header("Authorization", UUID.randomUUID().toString())
                .contentType(JSON)
                .body(new UpdateUserProfileRequest("updated_name", "updated_phone"))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_403_refreshTokenUsedInsteadOfAccessToken() {
        var refreshToken = mockUtil
                .doAuthenticationRequest(mockUtil.doConfirmedRegistration())
                .body()
                .jsonPath()
                .getString("refresh_token");
        given()
                .header("Authorization", refreshToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest("updated_name", "updated_phone"))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_403_blacklistedAccessToken() {
        var admin = mockUtil.savedAdmin();
        var user = mockUtil.savedUserProfileDto();
        var accessToken = mockUtil.doAuthentication(user.getEmail(), user.getPassword());
        var oauthTokens = mockUtil.toOAuthAccessTokenDtoList(user, accessToken);
        blacklistService.blacklistTokens(oauthTokens, admin.getId());
        given()
                .header("Authorization", accessToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest("updated_name", "updated_phone"))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_200_ok() {
        var accessToken = mockUtil.doAuthentication();
        given()
                .header("Authorization", accessToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest("updated_name", "updated_phone"))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
