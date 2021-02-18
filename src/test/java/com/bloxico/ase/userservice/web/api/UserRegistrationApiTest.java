package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.testutil.UtilUserProfile.newSubmitArtOwnerRequest;
import static com.bloxico.ase.testutil.UtilUserProfile.newSubmitUninvitedEvaluatorRequest;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.entity.user.Role.USER;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class UserRegistrationApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilToken mockUtil;
    @Autowired private TokenRepository tokenRepository;

    @Test
    public void registration_400_passwordMismatch() {
        var email = genEmail();
        given()
                .contentType(JSON)
                .body(new RegistrationRequest(email, email, email, genEmail(), Set.of()))
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.User.MATCH_REGISTRATION_PASSWORD_ERROR.getCode()));
    }

    @Test
    public void registration_409_userAlreadyExists() {
        var request = utilAuth.genRegistrationRequest();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_EXISTS.getCode()));
    }

    @Test
    public void registration_400_invalidAspirationName() {
        var email = genEmail();
        var request = new RegistrationRequest(email, email, email, email, Set.of(genUUID()));
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.ROLE_NOT_FOUND.getCode()));
    }

    @Test
    public void registration_200_ok() {
        given()
                .contentType(JSON)
                .body(utilAuth.genRegistrationRequest())
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body("token_value", not(isEmptyOrNullString()));
    }

    @Test
    public void registration_200_withAspirations() {
        given()
                .contentType(JSON)
                .body(utilAuth.genRegistrationRequestWithAspirations(USER, EVALUATOR))
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body("token_value", not(isEmptyOrNullString()));
    }

    @Test
    public void registrationConfirm_404_tokenNotFound() {
        utilAuth.doRegistration();
        var token = genUUID();
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(token))
                .when()
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationConfirm_200_ok() {
        var registration = utilAuth.doRegistration();
        var token = registration.getToken();
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(token))
                .when()
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void registrationTokenRefresh_404_tokenNotFound() {
        given()
                .when()
                .param(TOKEN_PARAM, genUUID())
                .get(API_URL + REGISTRATION_TOKEN_REFRESH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenRefresh_200_ok() {
        given()
                .when()
                .param(TOKEN_PARAM, utilAuth.doRegistration().getToken())
                .get(API_URL + REGISTRATION_TOKEN_REFRESH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void registrationTokenResend_404_userNotFound() {
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(genEmail()))
                .when()
                .post(API_URL + REGISTRATION_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenResend_404_tokenNotFound() {
        var registration = utilAuth.doRegistration();
        var tokenId = tokenRepository
                .findByValue(registration.getToken())
                .orElseThrow()
                .getId();
        tokenRepository.deleteById(tokenId);
        tokenRepository.flush();
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(registration.getEmail()))
                .when()
                .post(API_URL + REGISTRATION_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenResend_200_ok() {
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(utilAuth.doRegistration().getEmail()))
                .when()
                .post(API_URL + REGISTRATION_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void sendEvaluatorInvitation_409_evaluatorAlreadyInvited() {
        var registration = utilAuth.doConfirmedRegistration();
        var bearer = utilAuth.doAdminAuthentication();
        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        // send invitation to already invited user
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_EXISTS.getCode()));
    }

    @Test
    public void sendEvaluatorInvitation_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void checkEvaluatorInvitation_404_invitationNotFound() {
        given()
                .pathParam("token", genUUID())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void checkEvaluatorInvitation_200_ok() {
        given()
                .pathParam("token", mockUtil.savedInvitedPendingEvaluatorDto().getToken())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void resendEvaluatorInvitation_404_evaluatorNotInvited() {
        var registration = utilAuth.doConfirmedRegistration();
        var request = new EvaluatorInvitationResendRequest(registration.getEmail());
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_RESEND)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void resendEvaluatorInvitation_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var bearer = utilAuth.doAdminAuthentication();
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(new EvaluatorInvitationRequest(registration.getEmail()))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(new EvaluatorInvitationResendRequest(registration.getEmail()))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_RESEND)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void withdrawEvaluatorInvitation_404_evaluatorNotInvited() {
        var registration = utilAuth.doConfirmedRegistration();
        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void withdrawEvaluatorInvitation_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var bearer = utilAuth.doAdminAuthentication();
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(new EvaluatorInvitationRequest(registration.getEmail()))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", bearer)
                .contentType(JSON)
                .body(new EvaluatorInvitationWithdrawalRequest(registration.getEmail()))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void requestEvaluatorRegistration_409_evaluatorAlreadyPending() {
        var registration = utilAuth.doConfirmedRegistration();
        var bearerToken = utilAuth.doAuthentication(registration);
        var cvBytes = getTestCVBytes();
        given()
                .header("Authorization", bearerToken)
                .formParam("email", registration.getEmail())
                .multiPart("cv", "cv.txt", cvBytes)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", bearerToken)
                .formParam("email", registration.getEmail())
                .multiPart("cv", "cv.txt", cvBytes)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_EXISTS.getCode()));
    }

    @Test
    public void requestEvaluatorRegistration_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var cvBytes = getTestCVBytes();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("email", registration.getEmail())
                .multiPart("cv", "cv.txt", cvBytes)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void submitEvaluator_404_tokenNotFound() {
        given()
                .contentType(JSON)
                .body(newSubmitUninvitedEvaluatorRequest())
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    // TODO-test submitEvaluator_409_userAlreadyExists
    @Test
    public void submitEvaluator_409_userAlreadyExists() {
        var request = mockUtil.submitInvitedEvaluatorRequest();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(409);
    }

    @Test
    public void submitEvaluator_200_ok() {
        var request = mockUtil.submitInvitedEvaluatorRequest();
        var evaluator = given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(EvaluatorDto.class);
        assertNotNull(evaluator.getUserProfile().getId());
        assertNotNull(evaluator.getUserProfile().getUserId());
        assertEquals(evaluator.getUserProfile().getFirstName(), request.getFirstName());
        assertEquals(evaluator.getUserProfile().getLastName(), request.getLastName());
        assertEquals(evaluator.getUserProfile().getPhone(), request.getPhone());
        assertEquals(evaluator.getUserProfile().getBirthday(), request.getBirthday());
        assertEquals(evaluator.getUserProfile().getGender(), request.getGender());
        assertEquals(evaluator.getUserProfile().getLocation().getCity().getCountry().getName(), request.getCountry());
        assertEquals(evaluator.getUserProfile().getLocation().getCity().getZipCode(), request.getZipCode());
        assertEquals(evaluator.getUserProfile().getLocation().getCity().getName(), request.getCity());
        assertEquals(evaluator.getUserProfile().getLocation().getAddress(), request.getAddress());
    }

    @Test
    public void submitArtOwner_409_userAlreadyExists() {
        var request = newSubmitArtOwnerRequest();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(409);
    }

    @Test
    public void submitArtOwner_200_ok() {
        var request = newSubmitArtOwnerRequest();
        var artOwner = given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ArtOwnerDto.class);
        assertNotNull(artOwner.getUserProfile().getId());
        assertNotNull(artOwner.getUserProfile().getUserId());
        assertEquals(artOwner.getUserProfile().getFirstName(), request.getFirstName());
        assertEquals(artOwner.getUserProfile().getLastName(), request.getLastName());
        assertEquals(artOwner.getUserProfile().getPhone(), request.getPhone());
        assertEquals(artOwner.getUserProfile().getBirthday(), request.getBirthday());
        assertEquals(artOwner.getUserProfile().getGender(), request.getGender());
        assertEquals(artOwner.getUserProfile().getLocation().getCity().getCountry().getName(), request.getCountry());
        assertEquals(artOwner.getUserProfile().getLocation().getCity().getZipCode(), request.getZipCode());
        assertEquals(artOwner.getUserProfile().getLocation().getCity().getName(), request.getCity());
        assertEquals(artOwner.getUserProfile().getLocation().getAddress(), request.getAddress());
    }

    @Test
    public void searchPendingEvaluators_403_forbidden() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("email", "aseUser")
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void searchPendingEvaluators_200_ok() {
        var pe1 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var pe2 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var pe3 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("barFoo"));
        var pendingEvaluators = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("email", "fooBar")
                .param("page", 0)
                .param("size", MAX_VALUE)
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedPendingEvaluatorDataResponse.class)
                .getPendingEvaluators();
        assertThat(
                pendingEvaluators,
                allOf(
                        hasItems(pe1, pe2),
                        not(hasItems(pe3))));
    }

    // TODO-tests downloadEvaluatorResume_404_evaluatorNotPending

    // TODO-tests downloadEvaluatorResume_404_resumeNotFound

    // TODO-tests downloadEvaluatorResume_400_downloadFailed

    @Test
    public void downloadEvaluatorResume_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("email", registration.getEmail())
                .multiPart("cv", "cv.txt", getTestCVBytes())
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("email", registration.getEmail())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_RESUME_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(200).body(notNullValue());
    }

}
