package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.EvaluatorRepository;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

import static com.bloxico.ase.testutil.MockUtil.genEmail;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.junit.Assert.*;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserRegistrationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private PendingEvaluatorServiceImpl pendingEvaluatorService;

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    @Autowired
    private PendingEvaluatorRepository pendingEvaluatorRepository;

    @Autowired
    private UserRegistrationFacadeImpl userRegistrationFacade;

    @Test(expected = NullPointerException.class)
    public void registerUserWithVerificationToken_nullRequest() {
        userRegistrationFacade.registerUserWithVerificationToken(null);
    }

    @Test(expected = UserProfileException.class)
    public void registerUserWithVerificationToken_passwordMismatch() {
        var request = new RegistrationRequest("passwordMismatch@mail.com", "Password1!", "Password2!");
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test(expected = UserProfileException.class)
    public void registerUserWithVerificationToken_userAlreadyExists() {
        var request1 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        var request2 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(request1);
        userRegistrationFacade.registerUserWithVerificationToken(request2);
    }

    @Test(expected = UserProfileException.class)
    public void registerUserWithVerificationToken_invalidAspirationName() {
        var request = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        request.addAspirationName(uuid());
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test
    public void registerDisabledUser() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test(expected = NullPointerException.class)
    public void handleTokenValidation_nullRequest() {
        userRegistrationFacade.handleTokenValidation(null);
    }

    @Test(expected = UserProfileException.class)
    public void handleTokenValidation_userNotFound() {
        var invalid = uuid();
        var request = new TokenValidationRequest(invalid, invalid);
        userRegistrationFacade.handleTokenValidation(request);
    }

    @Test(expected = TokenException.class)
    public void handleTokenValidation_tokenNotFound() {
        var regRequest = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var invalid = uuid();
        var tknRequest = new TokenValidationRequest(regRequest.getEmail(), invalid);
        userRegistrationFacade.handleTokenValidation(tknRequest);
    }

    @Test
    public void handleTokenValidation() {
        var email = "passwordMatches@mail.com";
        var regRequest = new RegistrationRequest(email, "Password1!", "Password1!");
        var token = userRegistrationFacade.registerUserWithVerificationToken(regRequest).getTokenValue();
        var tknRequest = new TokenValidationRequest(email, token);
        userRegistrationFacade.handleTokenValidation(tknRequest);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(userProfileService.findUserProfileByEmail(email).getEnabled());
    }

    @Test(expected = NullPointerException.class)
    public void refreshExpiredToken_nullRequest() {
        userRegistrationFacade.refreshExpiredToken(null);
    }

    @Test(expected = TokenException.class)
    public void refreshExpiredToken_tokenNotFound() {
        var token = uuid();
        userRegistrationFacade.refreshExpiredToken(token);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void refreshExpiredToken() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var tokenValue = userRegistrationFacade.registerUserWithVerificationToken(request).getTokenValue();
        var originalTokenDto = MAPPER.toDto(
                tokenRepository
                        .findByValue(tokenValue)
                        .orElseThrow());
        userRegistrationFacade.refreshExpiredToken(tokenValue);
        var refreshedTokenDto = MAPPER.toDto(
                tokenRepository
                        .findByTypeAndUserId(REGISTRATION, originalTokenDto.getUserId())
                        .orElseThrow());
        assertEquals(originalTokenDto.getId(), refreshedTokenDto.getId());
        assertEquals(originalTokenDto.getUserId(), refreshedTokenDto.getUserId());
        assertNotEquals(originalTokenDto.getValue(), refreshedTokenDto.getValue());
        assertTrue(originalTokenDto.getExpiryDate().isBefore(refreshedTokenDto.getExpiryDate()));
    }

    @Test(expected = NullPointerException.class)
    public void resendVerificationToken_nullRequest() {
        userRegistrationFacade.resendVerificationToken(null);
    }

    @Test(expected = UserProfileException.class)
    public void resendVerificationToken_userNotFound() {
        var request = new ResendTokenRequest(uuid());
        userRegistrationFacade.resendVerificationToken(request);
    }

    @Test
    public void resendVerificationToken() {
        var email = "passwordMatches@mail.com";
        var regRequest = new RegistrationRequest(email, "Password1!", "Password1!");
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var resRequest = new ResendTokenRequest(email);
        userRegistrationFacade.resendVerificationToken(resRequest);
    }

    @Test(expected = NullPointerException.class)
    public void submitEvaluator_nullRequest() {
        userRegistrationFacade.submitEvaluator(null);
    }

    @Test(expected = TokenException.class)
    public void submitEvaluator_evaluatorNotPending() {
        var request = mockUtil.newSubmitUninvitedEvaluatorRequest();
        userRegistrationFacade.submitEvaluator(request);
    }

    @Test
    public void submitEvaluator_evaluatorPending() {
        var request = mockUtil.newSubmitInvitedEvaluatorRequest();
        assertTrue(evaluatorRepository.findAll().isEmpty());
        userRegistrationFacade.submitEvaluator(request);
        var userProfile = userProfileService.findUserProfileByEmail(request.getEmail());
        assertEquals(userProfile, userProfileService.findUserProfileByEmail(userProfile.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void sendEvaluatorInvitation_requestIsNull() {
        var admin = mockUtil.savedAdmin();

        userRegistrationFacade.sendEvaluatorInvitation(null, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void sendEvaluatorInvitation_evaluatorAlreadyInvited() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // send invitation to already invited user
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());
    }

    @Test
    public void sendEvaluatorInvitation_evaluatorAlreadyRequested() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), "storage.com/cv-123.docx");
        userRegistrationFacade.requestEvaluatorRegistration(registrationRequest, user.getId());

        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(pendingEvaluator);
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
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertNotNull(newlyCreatedPendingEvaluator.getToken());
        assertNull(newlyCreatedPendingEvaluator.getCvPath());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertSame(INVITED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void checkEvaluatorInvitation_nullToken() {
        userRegistrationFacade.checkEvaluatorInvitation(null);
    }

    @Test(expected = TokenException.class)
    public void checkEvaluatorInvitation_tokenNotFound() {
        userRegistrationFacade.checkEvaluatorInvitation(uuid());
    }

    @Test(expected = TokenException.class)
    public void checkEvaluatorInvitation_invitationTokenNotFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorRegistrationRequest(genEmail(), uuid());
        var pending = pendingEvaluatorService.createPendingEvaluator(request, principalId);
        userRegistrationFacade.checkEvaluatorInvitation(pending.getToken());
    }

    @Test
    public void checkEvaluatorInvitation() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = pendingEvaluatorService.createPendingEvaluator(request, principalId);
        userRegistrationFacade.checkEvaluatorInvitation(pending.getToken());
    }

    @Test(expected = NullPointerException.class)
    public void resendEvaluatorInvitation_requestIsNull() {
        userRegistrationFacade.resendEvaluatorInvitation(null);
    }

    @Test(expected = NullPointerException.class)
    public void resendEvaluatorInvitation_emailIsNull() {
        var request = new EvaluatorInvitationResendRequest(null);
        userRegistrationFacade.resendEvaluatorInvitation(request);
    }

    @Test(expected = TokenException.class)
    public void resendEvaluatorInvitation_evaluatorIsNotAlreadyInvited() {
        var request = new EvaluatorInvitationResendRequest("userNotFound@mail.com");
        userRegistrationFacade.resendEvaluatorInvitation(request);
    }

    @Test
    public void resendEvaluatorInvitation() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        // send invitation to user
        var sendInvitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(sendInvitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // now resend invitation
        var resendInvitationRequest = new EvaluatorInvitationResendRequest(user.getEmail());
        userRegistrationFacade.resendEvaluatorInvitation(resendInvitationRequest);
    }

    @Test(expected = NullPointerException.class)
    public void withdrawEvaluatorInvitation_requestIsNull() {
        userRegistrationFacade.withdrawEvaluatorInvitation(null);
    }

    @Test(expected = NullPointerException.class)
    public void withdrawEvaluatorInvitation_emailIsNull() {
        var request = new EvaluatorInvitationWithdrawalRequest(null);
        userRegistrationFacade.withdrawEvaluatorInvitation(request);
    }

    @Test(expected = TokenException.class)
    public void withdrawEvaluatorInvitation_evaluatorIsNotAlreadyInvited() {
        var request = new EvaluatorInvitationWithdrawalRequest("userNotFound@mail.com");
        userRegistrationFacade.withdrawEvaluatorInvitation(request);
    }

    @Test
    public void withdrawEvaluatorInvitation() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        // send invitation to user
        var sendInvitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(sendInvitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // now withdraw invitation
        var withdrawInvitationRequest = new EvaluatorInvitationWithdrawalRequest(user.getEmail());
        userRegistrationFacade.withdrawEvaluatorInvitation(withdrawInvitationRequest);

        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    // TODO next
    @Test(expected = NullPointerException.class)
    public void requestEvaluatorRegistration_requestIsNull() {
        var admin = mockUtil.savedAdmin();

        userRegistrationFacade.requestEvaluatorRegistration(null, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void requestEvaluatorRegistration_evaluatorAlreadyRegistered() {
        var user = mockUtil.savedUserProfile();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), "storage.com/cv-123.docx");
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // request registration again
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());
    }

    @Test(expected = TokenException.class)
    public void requestEvaluatorRegistration_evaluatorAlreadyInvited() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(invitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), "storage.com/cv-123.docx");
        userRegistrationFacade.requestEvaluatorRegistration(registrationRequest, user.getId());
    }

    @Test
    public void requestEvaluatorRegistration() {
        var user = mockUtil.savedUserProfile();

        var cvPath = "storage.com/cv-123.docx";
        var request = new EvaluatorRegistrationRequest(user.getEmail(), cvPath);
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertNotNull(newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertEquals(cvPath, newlyCreatedPendingEvaluator.getCvPath());
        assertSame(REQUESTED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    public void searchPendingEvaluators() {
        var emails = Arrays.asList("user2@mail.com", "user3@mail.com", "user1@mail.com");
        mockUtil.createInvitedPendingEvaluators(emails);

        var response = userRegistrationFacade
                .searchPendingEvaluators("user", 0, 2, "email");

        var pendingEvaluators = response.getPendingEvaluators();

        assertNotNull(response);
        assertNotNull(pendingEvaluators);
        assertEquals(2, pendingEvaluators.size());
        assertEquals("user1@mail.com", pendingEvaluators.get(0).getEmail());
        assertEquals("user2@mail.com", pendingEvaluators.get(1).getEmail());
    }

}
