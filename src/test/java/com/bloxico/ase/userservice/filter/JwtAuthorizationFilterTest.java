package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_UPDATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class JwtAuthorizationFilterTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private ITokenBlacklistService blacklistService;

    @Test
    public void doFilterInternal_403_invalidAccessToken() {
        given()
                .header("Authorization", uuid())
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(uuid(), uuid(), uuid()))
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
                .body(new UpdateUserProfileRequest(uuid(), uuid(), uuid()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_403_blacklistedAccessToken() {
        var registration = mockUtil.doConfirmedRegistration();
        mockUtil.savedUserProfile(registration.getId());
        var accessToken = mockUtil.doAuthentication(registration);
        var email = registration.getEmail();
        var oauthToken = mockUtil.toOAuthAccessTokenDto(email, accessToken);
        var admin = mockUtil.savedAdmin();
        blacklistService.blacklistTokens(List.of(oauthToken), admin.getId());
        given()
                .header("Authorization", accessToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(uuid(), uuid(), uuid()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_200_ok() {
        var registration = mockUtil.doConfirmedRegistration();
        mockUtil.savedUserProfile(registration.getId());
        given()
                .header("Authorization", mockUtil.doAuthentication(registration))
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(uuid(), uuid(), uuid()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
