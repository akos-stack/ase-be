package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.facade.impl.UserRegistrationFacadeImpl;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.entity.user.Role.USER;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class UserRegistrationApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilToken mockUtil;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private PendingEvaluatorRepository pendingEvaluatorRepository;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UserRegistrationFacadeImpl userRegistrationFacade;
    @Autowired private UtilUser utilUser;

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
        var name = genUUID() + ".txt";
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("email", registration.getEmail())
                .multiPart("cv", name, cvBytes)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void submitEvaluator_404_tokenNotFound() {
        given()
                .formParams(utilUserProfile.genSaveEvaluatorFormParams(genUUID(), genEmail()))
                .multiPart("profileImage", genUUID() + ".jpg", getTestImageBytes())
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    // TODO-test submitEvaluator_404_countryNotFound

    // TODO-test submitEvaluator_409_userAlreadyExists

    @Test
    public void submitEvaluator_200_ok() {
        var request = utilUserProfile.genSaveEvaluatorFormParams();
        var evaluatorDto = given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", getTestImageBytes())
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(EvaluatorDto.class);
        assertNotNull(evaluatorDto.getUserProfile().getId());
        assertNotNull(evaluatorDto.getUserProfile().getUserId());
        assertEquals(evaluatorDto.getUserProfile().getFirstName(), request.get("firstName"));
        assertEquals(evaluatorDto.getUserProfile().getLastName(), request.get("lastName"));
        assertEquals(evaluatorDto.getUserProfile().getPhone(), request.get("phone"));
        assertEquals(evaluatorDto.getUserProfile().getBirthday(), LocalDate.parse(request.get("birthday")));
        assertEquals(evaluatorDto.getUserProfile().getGender(), request.get("gender"));
        assertEquals(evaluatorDto.getUserProfile().getLocation().getCountry().getName(), request.get("country"));
        assertEquals(evaluatorDto.getUserProfile().getLocation().getAddress(), request.get("address"));
    }

    // TODO-test submitArtOwner_404_countryNotFound

    @Test
    public void submitArtOwner_409_userAlreadyExists() {
        var imageBytes = getTestImageBytes();
        var formParams = utilUserProfile.genSaveArtOwnerFormParams();
        given()
                .formParams(formParams)
                .multiPart("profileImage", genUUID() + ".jpg", imageBytes)
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .formParams(formParams)
                .multiPart("profileImage", genUUID() + ".jpg", imageBytes)
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(409);
    }

    @Test
    public void submitArtOwner_200_ok() {
        var request = utilUserProfile.genSaveArtOwnerFormParams();
        var artOwnerDto = given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", getTestImageBytes())
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ArtOwnerDto.class);
        assertNotNull(artOwnerDto.getUserProfile().getId());
        assertNotNull(artOwnerDto.getUserProfile().getUserId());
        assertEquals(artOwnerDto.getUserProfile().getFirstName(), request.get("firstName"));
        assertEquals(artOwnerDto.getUserProfile().getLastName(), request.get("lastName"));
        assertEquals(artOwnerDto.getUserProfile().getPhone(), request.get("phone"));
        assertEquals(artOwnerDto.getUserProfile().getBirthday(), LocalDate.parse(request.get("birthday")));
        assertEquals(artOwnerDto.getUserProfile().getGender(), request.get("gender"));
        assertEquals(artOwnerDto.getUserProfile().getLocation().getCountry().getName(), request.get("country"));
        assertEquals(artOwnerDto.getUserProfile().getLocation().getAddress(), request.get("address"));
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

    @Test
    public void downloadEvaluatorResume_404_resumeNotFound() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("email", registration.getEmail())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_RESUME_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.RESUME_NOT_FOUND.getCode()));
    }

    @Test
    public void downloadEvaluatorResume_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var name = genUUID() + ".txt";
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("email", registration.getEmail())
                .multiPart("cv", name, getTestCVBytes())
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
