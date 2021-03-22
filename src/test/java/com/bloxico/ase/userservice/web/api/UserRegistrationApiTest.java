package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.entity.token.Token;
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
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.COUNTRY_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_EXISTS;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class UserRegistrationApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilToken utilToken;
    @Autowired private UtilUser utilUser;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private PendingEvaluatorRepository pendingEvaluatorRepository;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UserRegistrationFacadeImpl userRegistrationFacade;
    @Autowired private UtilSecurityContext securityContext;

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
                .body(ERROR_CODE, is(USER_EXISTS.getCode()));
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
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
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
                .param("token", genUUID())
                .get(API_URL + REGISTRATION_TOKEN_REFRESH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void registrationTokenRefresh_200_ok() {
        given()
                .when()
                .param("token", utilAuth.doRegistration().getToken())
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
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
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
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void checkEvaluatorInvitation_200_ok() {
        given()
                .pathParam("token", utilToken.savedInvitedPendingEvaluatorDto().getToken())
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
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
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
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
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
        var cvBytes = genFileBytes(CV);
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
        var cvBytes = genFileBytes(CV);
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
    @WithMockCustomUser
    public void submitEvaluator_404_tokenNotFound() {
        var request = utilUserProfile.genSaveEvaluatorFormParams();
        request.put("token", genUUID());
        given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void submitEvaluator_404_countryNotFound() {
        var request = utilUserProfile.genSaveEvaluatorFormParams();
        request.put("country", genUUID());
        given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void submitEvaluator_409_userAlreadyExists() {
        var request = utilUserProfile.genSaveEvaluatorFormParams();
        utilUser.savedUserDtoWithEmail(request.get("email"));
        given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_EVALUATOR_SUBMIT)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(USER_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void submitEvaluator_200_ok() {
        var request = utilUserProfile.genSaveEvaluatorFormParams();
        var evaluatorDto = given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
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

    @Test
    public void submitArtOwner_404_countryNotFound() {
        var request = utilUserProfile.genSaveArtOwnerFormParams();
        request.put("country", genUUID());
        given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + REGISTRATION_ART_OWNER_SUBMIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Location.COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void submitArtOwner_409_userAlreadyExists() {
        var imageBytes = genFileBytes(IMAGE);
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
    @WithMockCustomUser
    public void submitArtOwner_200_ok() {
        var request = utilUserProfile.genSaveArtOwnerFormParams();
        var artOwnerDto = given()
                .formParams(request)
                .multiPart("profileImage", genUUID() + ".jpg", genFileBytes(IMAGE))
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
                .params(allPages("email", genEmail()))
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchPendingEvaluators_200_ok() {
        var email = genEmail();
        var pe1 = utilToken.savedInvitedPendingEvaluatorDto(genEmail(email));
        var pe2 = utilToken.savedInvitedPendingEvaluatorDto(genEmail(email));
        var pe3 = utilToken.savedInvitedPendingEvaluatorDto(genEmail());
        var pendingEvaluators = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("email", email))
                .when()
                .get(API_URL + REGISTRATION_EVALUATOR_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchPendingEvaluatorsResponse.class)
                .getPage()
                .getContent();
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
                .multiPart("cv", name, genFileBytes(CV))
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
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    public void sendHostInvitation_404_userNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationRequest(-1L))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void sendHostInvitation_409_hostAlreadyInvited() {
        var userId = utilUser.savedUser().getId();
        var request = new HostInvitationRequest(userId);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_EXISTS.getCode()));
    }

    @Test
    public void sendHostInvitation_200_ok() {
        var userId = utilUser.savedUser().getId();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void sendHostInvitation_200_ok_specific_role() {
        var userId = utilUserProfile.savedEvaluator().getUserProfile().getUserId();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void withdrawHostInvitation_404_tokenNotFound_userNotExists() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationWithdrawalRequest(-1L))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void withdrawHostInvitation_404_tokenNotFound_userNotInvited() {
        var userId = utilUser.savedUser().getId();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationWithdrawalRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void withdrawHostInvitation_404_withdrawWithdrawnUser() {
        var userId = utilUser.savedUser().getId();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationWithdrawalRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationWithdrawalRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void withdrawHostInvitation_200_ok() {
        var userId = utilUser.savedUser().getId();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new HostInvitationWithdrawalRequest(userId))
                .when()
                .post(API_URL + REGISTRATION_HOST_INVITATION_WITHDRAW)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void checkHostInvitation_404_invitationNotFound() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .pathParam("token", genUUID())
                .when()
                .get(API_URL + REGISTRATION_HOST_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser
    public void checkHostInvitation_200_ok() {
        var loggedInUser = utilSecurityContext.getLoggedInUserId();
        var request = new HostInvitationRequest(loggedInUser);
        userRegistrationFacade.sendHostInvitation(request);
        var token = tokenRepository.findByTypeAndUserId(Token.Type.HOST_INVITATION, loggedInUser);
        given()
                .pathParam("token", token.get().getValue())
                .when()
                .get(API_URL + REGISTRATION_HOST_INVITATION_CHECK)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
