package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.MockUtil.*;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UserRegistrationApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PendingEvaluatorRepository pendingEvaluatorRepository;

    @Test
    public void registration_200_ok() {
        given()
                .contentType(JSON)
                .body(new RegistrationRequest(
                        "passwordMatches@mail.com",
                        "Password1!", "Password1!"))
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body("token_value", not(isEmptyOrNullString()));
    }

    @Test
    public void registration_400_passwordMismatch() {
        given()
                .contentType(JSON)
                .body(new RegistrationRequest(
                        "passwordMismatch@mail.com",
                        "Password1!", "Password2!"))
                .when()
                .post(API_URL + REGISTRATION_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.User.MATCH_REGISTRATION_PASSWORD_ERROR.getCode()));
    }

    @Test
    public void registration_409_userAlreadyExists() {
        var request = new RegistrationRequest(
                "passwordMatches@mail.com",
                "Password1!", "Password1!");
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
    public void registrationConfirm_200_ok() {
        var registration = mockUtil.doRegistration();
        var email = registration.getEmail();
        var token = registration.getToken();
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(email, token))
                .when()
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void registrationConfirm_404_userNotFound() {
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest("userNotFound@mail.com", uuid()))
                .when()
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationConfirm_404_tokenNotFound() {
        var email = mockUtil.doRegistration().getEmail();
        var token = uuid();
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(email, token))
                .when()
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenRefresh_200_ok() {
        given()
                .when()
                .param(TOKEN_PARAM, mockUtil.doRegistration().getToken())
                .get(API_URL + REGISTRATION_TOKEN_REFRESH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void registrationTokenRefresh_404_tokenNotFound() {
        given()
                .when()
                .param(TOKEN_PARAM, uuid())
                .get(API_URL + REGISTRATION_TOKEN_REFRESH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenResend_200_ok() {
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(mockUtil.doRegistration().getEmail()))
                .when()
                .post(API_URL + REGISTRATION_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void registrationTokenResend_404_userNotFound() {
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest("userNotFound@mail.com"))
                .when()
                .post(API_URL + REGISTRATION_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenResend_404_tokenNotFound() {
        var registration = mockUtil.doRegistration();
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
    public void sendEvaluatorInvitation_200_invitationSuccessfullySent() {
        var registration = mockUtil.doConfirmedRegistration();

        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void sendEvaluatorInvitation_409_evaluatorIsAlreadyInvited() {
        var registration = mockUtil.doConfirmedRegistration();
        var adminBearerToken = mockUtil.doAdminAuthentication();

        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);

        // send invitation to already invited user
        given()
                .header("Authorization", adminBearerToken)
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
    public void checkEvaluatorInvitation_200_invitationExists() {
        var principalId = mockUtil.savedAdmin().getId();
        var invitation = new PendingEvaluator();
        invitation.setEmail(genEmail());
        invitation.setStatus(INVITED);
        invitation.setToken(uuid());
        invitation.setCreatorId(principalId);
        pendingEvaluatorRepository.saveAndFlush(invitation);
        given()
                .pathParam("token", invitation.getToken())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void checkEvaluatorInvitation_404_invitationNotFound() {
        given()
                .pathParam("token", uuid())
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void resendEvaluatorInvitation_200_invitationSuccessfullyResent() {
        var registration = mockUtil.doConfirmedRegistration();
        var adminBearerToken = mockUtil.doAdminAuthentication();

        // send invitation first
        var sendInvitationRequest = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(sendInvitationRequest)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);

        // now resend invitation
        var resendInvitationRequest = new EvaluatorInvitationResendRequest(registration.getEmail());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(resendInvitationRequest)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_RESEND)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void resendEvaluatorInvitation_404_evaluatorIsNotAlreadyInvited() {
        var registration = mockUtil.doConfirmedRegistration();

        var request = new EvaluatorInvitationResendRequest(registration.getEmail());
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_RESEND)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void withdrawEvaluatorInvitation_200_invitationSuccessfullyWithdrawn() {
        var registration = mockUtil.doConfirmedRegistration();
        var adminBearerToken = mockUtil.doAdminAuthentication();

        // send invitation first
        var sendInvitationRequest = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(sendInvitationRequest)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);

        // now withdraw invitation
        var withdrawInvitationRequest = new EvaluatorInvitationWithdrawalRequest(registration.getEmail());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(withdrawInvitationRequest)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void withdrawEvaluatorInvitation_404_evaluatorIsNotAlreadyInvited() {
        var registration = mockUtil.doConfirmedRegistration();

        var request = new EvaluatorInvitationRequest(registration.getEmail());
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
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
    public void requestEvaluatorRegistration_200_pendingEvaluatorSuccessfullyRegistered() {
        var registration = mockUtil.doConfirmedRegistration();

        var request = new EvaluatorRegistrationRequest(registration.getEmail(), MockUtil.createMultipartFile());
        given()
                .header("Authorization", mockUtil.doAuthentication(registration))
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void requestEvaluatorRegistration_409_evaluatorIsAlreadyPending() {
        var registration = mockUtil.doConfirmedRegistration();
        var bearerToken = mockUtil.doAuthentication(registration);

        var request = new EvaluatorRegistrationRequest(registration.getEmail(), MockUtil.createMultipartFile());
        given()
                .header("Authorization", bearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", bearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_REQUEST)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_EXISTS.getCode()));
    }

    @Test
    public void submitEvaluator_200_ok() {
        var request = mockUtil.newSubmitInvitedEvaluatorRequest();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "user_profile.id", notNullValue(),
                        "user_profile.name", is(request.getUsername()),
                        "user_profile.password", is(request.getPassword()),
                        "user_profile.email", is(request.getEmail()),
                        "user_profile.enabled", is(true));
    }

    @Test
    public void submitEvaluator_404_tokenNotFound() {
        given()
                .contentType(JSON)
                .body(mockUtil.newSubmitUninvitedEvaluatorRequest())
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void searchPendingEvaluators_200_paginatedListSuccessfullyRetrieved() {
        var pendingEvaluators = mockUtil.createInvitedPendingEvaluators();

        var emailSearch = "aseUser";
        var pageIndex = 1;
        var pageSize = 4;

        var expectedList = pendingEvaluators
                .stream()
                .filter(p -> p.getEmail().contains(emailSearch))
                .sorted(Comparator.comparing(PendingEvaluatorDto::getEmail))
                .skip(pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        var actualList = given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("email", emailSearch)
                .param("page", pageIndex)
                .param("size", pageSize)
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("pending_evaluators", PendingEvaluatorDto.class);

        assertEquals(expectedList, actualList);
    }

    @Test
    public void searchPendingEvaluators_403_unauthorizedUser() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("email", "aseUser")
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(403);
    }

}
