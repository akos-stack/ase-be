package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepository;
import com.bloxico.ase.userservice.repository.user.profile.EvaluatorRepository;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.testutil.UtilUserProfile.newSubmitArtOwnerRequest;
import static com.bloxico.ase.testutil.UtilUserProfile.newSubmitUninvitedEvaluatorRequest;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.entity.user.Role.USER;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRegistrationFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilUser utilUser;
    @Autowired private UtilToken utilToken;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private UserServiceImpl userService;
    @Autowired private PendingEvaluatorServiceImpl pendingEvaluatorService;
    @Autowired private EvaluatorRepository evaluatorRepository;
    @Autowired private ArtOwnerRepository artOwnerRepository;
    @Autowired private PendingEvaluatorRepository pendingEvaluatorRepository;
    @Autowired private UserRegistrationFacadeImpl userRegistrationFacade;

    @Test
    public void registerUserWithVerificationToken_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.registerUserWithVerificationToken(null));
    }

    @Test
    public void registerUserWithVerificationToken_passwordMismatch() {
        var request = utilAuth.genRegistrationRequestPasswordMismatch();
        assertThrows(
                UserException.class,
                () -> userRegistrationFacade.registerUserWithVerificationToken(request));
    }

    @Test
    public void registerUserWithVerificationToken_userAlreadyExists() {
        var request = utilAuth.genRegistrationRequest();
        userRegistrationFacade.registerUserWithVerificationToken(request);
        assertThrows(
                UserException.class,
                () -> userRegistrationFacade.registerUserWithVerificationToken(request));
    }

    @Test
    public void registerUserWithVerificationToken_invalidAspirationName() {
        var request = utilAuth.genRegistrationRequestWithAspirations(genUUID());
        assertThrows(
                UserException.class,
                () -> userRegistrationFacade.registerUserWithVerificationToken(request));
    }

    @Test
    public void registerDisabledUser() {
        var request = utilAuth.genRegistrationRequest();
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test
    public void registerDisabledUser_withAspirations() {
        var request = utilAuth.genRegistrationRequestWithAspirations(USER, EVALUATOR);
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test
    public void handleTokenValidation_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.handleTokenValidation(null));
    }

    @Test
    public void handleTokenValidation_tokenNotFound() {
        var regRequest = utilAuth.genRegistrationRequest();
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var tknRequest = new TokenValidationRequest(regRequest.getEmail());
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.handleTokenValidation(tknRequest));
    }

    @Test
    public void handleTokenValidation() {
        var regRequest = utilAuth.genRegistrationRequest();
        var email = regRequest.getEmail();
        var token = userRegistrationFacade.registerUserWithVerificationToken(regRequest).getTokenValue();
        var tknRequest = new TokenValidationRequest(token);
        userRegistrationFacade.handleTokenValidation(tknRequest);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(userService.findUserByEmail(email).getEnabled());
    }

    @Test
    public void refreshExpiredToken_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.refreshExpiredToken(null));
    }

    @Test
    public void refreshExpiredToken_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.refreshExpiredToken(genUUID()));
    }

    @Test
    public void refreshExpiredToken() {
        var request = utilAuth.genRegistrationRequest();
        var tokenValue = userRegistrationFacade
                .registerUserWithVerificationToken(request)
                .getTokenValue();
        var originalTokenDto = tokenRepository
                .findByValue(tokenValue)
                .map(MAPPER::toDto)
                .orElseThrow();
        userRegistrationFacade.refreshExpiredToken(tokenValue);
        var refreshedTokenDto = tokenRepository
                .findByTypeAndUserId(REGISTRATION, originalTokenDto.getUserId())
                .map(MAPPER::toDto)
                .orElseThrow();
        assertEquals(originalTokenDto.getId(), refreshedTokenDto.getId());
        assertEquals(originalTokenDto.getUserId(), refreshedTokenDto.getUserId());
        assertNotEquals(originalTokenDto.getValue(), refreshedTokenDto.getValue());
        assertTrue(originalTokenDto.getExpiryDate().isBefore(refreshedTokenDto.getExpiryDate()));
    }

    @Test
    public void resendVerificationToken_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.resendVerificationToken(null));
    }

    @Test
    public void resendVerificationToken_userNotFound() {
        var request = new ResendTokenRequest(genUUID());
        assertThrows(
                UserException.class,
                () -> userRegistrationFacade.resendVerificationToken(request));
    }

    @Test
    public void resendVerificationToken() {
        var regRequest = utilAuth.genRegistrationRequest();
        var email = regRequest.getEmail();
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var resRequest = new ResendTokenRequest(email);
        userRegistrationFacade.resendVerificationToken(resRequest);
    }

    @Test
    public void sendEvaluatorInvitation_requestIsNull() {
        var admin = utilUser.savedAdmin();
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.sendEvaluatorInvitation(null, admin.getId()));
    }

    @Test
    public void sendEvaluatorInvitation_evaluatorAlreadyInvited() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId()));
    }

    @Test
    public void sendEvaluatorInvitation_evaluatorAlreadyRequested() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();
        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        userRegistrationFacade.requestEvaluatorRegistration(registrationRequest, user.getId());

        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElseThrow();

        assertNull(pendingEvaluator.getUpdaterId());
        assertSame(REQUESTED, pendingEvaluator.getStatus());

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(invitationRequest, admin.getId());

        assertNotNull(pendingEvaluator);
        assertEquals(admin.getId(), pendingEvaluator.getUpdaterId());
        assertSame(INVITED, pendingEvaluator.getStatus());
    }

    @Test
    public void sendEvaluatorInvitation() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElseThrow();

        assertNotNull(newlyCreatedPendingEvaluator.getToken());
        assertNull(newlyCreatedPendingEvaluator.getCvPath());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertSame(INVITED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    public void checkEvaluatorInvitation_nullToken() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.checkEvaluatorInvitation(null));
    }

    @Test
    public void checkEvaluatorInvitation_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.checkEvaluatorInvitation(genUUID()));
    }

    @Test
    public void checkEvaluatorInvitation_invitationTokenNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var request = new EvaluatorRegistrationRequest(genEmail(), genMultipartFile(pdf));
        var pending = pendingEvaluatorService.createPendingEvaluator(request, principalId);
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.checkEvaluatorInvitation(pending.getToken()));
    }

    @Test
    public void checkEvaluatorInvitation() {
        var principalId = utilUser.savedAdmin().getId();
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = pendingEvaluatorService.createPendingEvaluator(request, principalId);
        userRegistrationFacade.checkEvaluatorInvitation(pending.getToken());
    }

    @Test
    public void resendEvaluatorInvitation_requestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.resendEvaluatorInvitation(null));
    }

    @Test
    public void resendEvaluatorInvitation_emailIsNull() {
        var request = new EvaluatorInvitationResendRequest(null);
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.resendEvaluatorInvitation(request));
    }

    @Test
    public void resendEvaluatorInvitation_evaluatorIsNotAlreadyInvited() {
        var request = new EvaluatorInvitationResendRequest(genEmail());
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.resendEvaluatorInvitation(request));
    }

    @Test
    public void resendEvaluatorInvitation() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();

        // send invitation
        var sendInvitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(sendInvitationRequest, admin.getId());
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        // resend invitation
        var resendInvitationRequest = new EvaluatorInvitationResendRequest(user.getEmail());
        userRegistrationFacade.resendEvaluatorInvitation(resendInvitationRequest);
    }

    @Test
    public void withdrawEvaluatorInvitation_requestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.withdrawEvaluatorInvitation(null));
    }

    @Test
    public void withdrawEvaluatorInvitation_emailIsNull() {
        var request = new EvaluatorInvitationWithdrawalRequest(null);
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.withdrawEvaluatorInvitation(request));
    }

    @Test
    public void withdrawEvaluatorInvitation_evaluatorIsNotAlreadyInvited() {
        var request = new EvaluatorInvitationWithdrawalRequest(genEmail());
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.withdrawEvaluatorInvitation(request));
    }

    @Test
    public void withdrawEvaluatorInvitation() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();

        // send invitation to user
        var sendInvitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(sendInvitationRequest, admin.getId());
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        // withdraw invitation
        var withdrawInvitationRequest = new EvaluatorInvitationWithdrawalRequest(user.getEmail());
        userRegistrationFacade.withdrawEvaluatorInvitation(withdrawInvitationRequest);

        assertFalse(utilToken.isEvaluatorPending(user.getEmail()));
    }

    @Test
    public void submitEvaluator_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.submitEvaluator(null));
    }

    @Test
    public void submitEvaluator_evaluatorNotPending() {
        var request = newSubmitUninvitedEvaluatorRequest();
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.submitEvaluator(request));
    }

    @Test
    public void submitEvaluator_evaluatorPending() {
        var request = utilToken.submitInvitedEvaluatorRequest();
        assertTrue(evaluatorRepository.findAll().isEmpty());
        var evaluatorId = userRegistrationFacade.submitEvaluator(request).getId();
        assertTrue(evaluatorRepository.findById(evaluatorId).isPresent());
    }

    @Test
    public void submitArtOwner_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.submitArtOwner(null));
    }

    @Test
    public void submitArtOwner_userAlreadyExists() {
        var request = newSubmitArtOwnerRequest();
        userRegistrationFacade.submitArtOwner(request);
        assertThrows(
                UserException.class,
                () -> userRegistrationFacade.submitArtOwner(request));
    }

    @Test
    public void submitArtOwner() {
        var request = newSubmitArtOwnerRequest();
        assertTrue(artOwnerRepository.findAll().isEmpty());
        var artOwnerId = userRegistrationFacade.submitArtOwner(request).getId();
        assertTrue(artOwnerRepository.findById(artOwnerId).isPresent());
    }

    @Test
    public void requestEvaluatorRegistration_requestIsNull() {
        var admin = utilUser.savedAdmin();
        assertThrows(
                NullPointerException.class,
                () -> userRegistrationFacade.requestEvaluatorRegistration(null, admin.getId()));
    }

    @Test
    public void requestEvaluatorRegistration_evaluatorAlreadyRegistered() {
        var user = utilUser.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.requestEvaluatorRegistration(request, user.getId()));
    }

    @Test
    public void requestEvaluatorRegistration_evaluatorAlreadyInvited() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(invitationRequest, admin.getId());
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        assertThrows(
                TokenException.class,
                () -> userRegistrationFacade.requestEvaluatorRegistration(registrationRequest, user.getId()));
    }

    @Test
    public void requestEvaluatorRegistration() {
        var user = utilUser.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElseThrow();

        assertNotNull(newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertNotNull(newlyCreatedPendingEvaluator.getCvPath());
        assertSame(REQUESTED, newlyCreatedPendingEvaluator.getStatus());
    }

    // TODO-TEST searchPendingEvaluators_nullEmail

    // TODO-TEST searchPendingEvaluators_emptyResultSet

    @Test
    public void searchPendingEvaluators() {
        var pe1 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var pe2 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var pe3 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("barFoo"));
        assertThat(
                userRegistrationFacade
                        .searchPendingEvaluators("fooBar", 0, 2, "email")
                        .getPendingEvaluators(),
                allOf(
                        hasItems(pe1, pe2),
                        not(hasItems(pe3))));
    }

    // TODO-TEST downloadEvaluatorResume_nullEmail

    // TODO-TEST downloadEvaluatorResume_pendingEvaluatorNotFound

    // TODO-TEST downloadEvaluatorResume_resumeNotFound

    @Test
    public void downloadEvaluatorResume() {
        var user = utilUser.savedUser();
        var admin = utilUser.savedAdmin();
        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());
        var response = userRegistrationFacade.downloadEvaluatorResume(user.getEmail(), admin.getId());
        assertNotNull(response);
    }

}
