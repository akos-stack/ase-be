package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.UserProfileApi.MY_PROFILE_UPDATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class JwtAuthorizationFilterTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilToken utilToken;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private ITokenBlacklistService blacklistService;

    @Test
    public void doFilterInternal_403_invalidAccessToken() {
        given()
                .header("Authorization", genUUID())
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_403_refreshTokenUsedInsteadOfAccessToken() {
        var refreshToken = utilAuth
                .doAuthenticationRequest(utilAuth.doConfirmedRegistration())
                .body()
                .jsonPath()
                .getString("refresh_token");
        given()
                .header("Authorization", refreshToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser
    public void doFilterInternal_403_blacklistedAccessToken() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedUserProfile(registration.getId());
        var accessToken = utilAuth.doAuthentication(registration);
        var email = registration.getEmail();
        var oauthToken = utilToken.toOAuthAccessTokenDto(email, accessToken);
        blacklistService.blacklistTokens(List.of(oauthToken));
        given()
                .header("Authorization", accessToken)
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void doFilterInternal_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedUserProfile(registration.getId());
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType(JSON)
                .body(new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID()))
                .when()
                .post(API_URL + MY_PROFILE_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
