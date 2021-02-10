package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepository;
import com.bloxico.ase.userservice.repository.user.profile.EvaluatorRepository;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.genEmail;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.junit.Assert.*;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserRegistrationFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PendingEvaluatorServiceImpl pendingEvaluatorService;

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    @Autowired
    private ArtOwnerRepository artOwnerRepository;

    @Autowired
    private PendingEvaluatorRepository pendingEvaluatorRepository;

    @Autowired
    private UserRegistrationFacadeImpl userRegistrationFacade;

    @Test(expected = NullPointerException.class)
    public void registerUserWithVerificationToken_nullRequest() {
        userRegistrationFacade.registerUserWithVerificationToken(null);
    }

    @Test(expected = UserException.class)
    public void registerUserWithVerificationToken_passwordMismatch() {
        var email = genEmail();
        var request = new RegistrationRequest(email, email, email, uuid(), Set.of());
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test(expected = UserException.class)
    public void registerUserWithVerificationToken_userAlreadyExists() {
        var request = mockUtil.genRegistrationRequest();
        userRegistrationFacade.registerUserWithVerificationToken(request);
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test(expected = UserException.class)
    public void registerUserWithVerificationToken_invalidAspirationName() {
        var email = genEmail();
        var request = new RegistrationRequest(email, email, email, email, Set.of(uuid()));
        userRegistrationFacade.registerUserWithVerificationToken(request);
    }

    @Test
    public void registerDisabledUser() {
        var email = genEmail();
        var request = new RegistrationRequest(email, email, email, email, Set.of());
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test
    public void registerDisabledUser_withAspirations() {
        var email = genEmail();
        var aspirations = Set.of(
                mockUtil.getUserAspiration().getName(),
                mockUtil.getEvaluatorAspiration().getName());
        var request = new RegistrationRequest(email, email, email, email, aspirations);
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        assertNotNull(response.getTokenValue());
    }

    @Test(expected = NullPointerException.class)
    public void handleTokenValidation_nullRequest() {
        userRegistrationFacade.handleTokenValidation(null);
    }

    @Test(expected = TokenException.class)
    public void handleTokenValidation_tokenNotFound() {
        var regRequest = mockUtil.genRegistrationRequest();
        userRegistrationFacade.registerUserWithVerificationToken(regRequest);
        var tknRequest = new TokenValidationRequest(regRequest.getEmail());
        userRegistrationFacade.handleTokenValidation(tknRequest);
    }

    @Test
    public void handleTokenValidation() {
        var regRequest = mockUtil.genRegistrationRequest();
        var email = regRequest.getEmail();
        var token = userRegistrationFacade.registerUserWithVerificationToken(regRequest).getTokenValue();
        var tknRequest = new TokenValidationRequest(token);
        userRegistrationFacade.handleTokenValidation(tknRequest);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
        assertTrue(userService.findUserByEmail(email).getEnabled());
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
        var request = mockUtil.genRegistrationRequest();
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

    @Test(expected = UserException.class)
    public void resendVerificationToken_userNotFound() {
        var request = new ResendTokenRequest(uuid());
        userRegistrationFacade.resendVerificationToken(request);
    }

    @Test
    public void resendVerificationToken() {
        var regRequest = mockUtil.genRegistrationRequest();
        var email = regRequest.getEmail();
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
        var evaluatorId = userRegistrationFacade.submitEvaluator(request).getId();
        assertTrue(evaluatorRepository.findById(evaluatorId).isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void submitArtOwner_nullRequest() {
        userRegistrationFacade.submitArtOwner(null);
    }

    @Test(expected = UserException.class)
    public void submitArtOwner_userAlreadyExists() {
        var request = mockUtil.newSubmitArtOwnerRequest();
        userRegistrationFacade.submitArtOwner(request);
        userRegistrationFacade.submitArtOwner(request);
    }

    @Test
    public void submitArtOwner() {
        var request = mockUtil.newSubmitArtOwnerRequest();
        assertTrue(artOwnerRepository.findAll().isEmpty());
        var artOwnerId = userRegistrationFacade.submitArtOwner(request).getId();
        assertTrue(artOwnerRepository.findById(artOwnerId).isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void sendEvaluatorInvitation_requestIsNull() {
        var admin = mockUtil.savedAdmin();

        userRegistrationFacade.sendEvaluatorInvitation(null, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void sendEvaluatorInvitation_evaluatorAlreadyInvited() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // send invitation to already invited user
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());
    }

    @Test
    public void sendEvaluatorInvitation_evaluatorAlreadyRequested() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();
        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
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
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(request, admin.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertNotNull(newlyCreatedPendingEvaluator.getToken());
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
        var request = new EvaluatorRegistrationRequest(genEmail(), MockUtil.createMultipartFile());
        var pending = pendingEvaluatorService.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request), principalId);
        userRegistrationFacade.checkEvaluatorInvitation(pending.getToken());
    }

    @Test
    public void checkEvaluatorInvitation() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = pendingEvaluatorService.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request), principalId);
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
        var user = mockUtil.savedUser();
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
        var user = mockUtil.savedUser();
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
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        // request registration again
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());
    }

    @Test(expected = TokenException.class)
    public void requestEvaluatorRegistration_evaluatorAlreadyInvited() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        userRegistrationFacade.sendEvaluatorInvitation(invitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        userRegistrationFacade.requestEvaluatorRegistration(registrationRequest, user.getId());
    }

    @Test
    public void requestEvaluatorRegistration() {
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());

        var newlyCreatedPendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertNotNull(newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
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

    @Test
    public void downloadEvaluatorResume() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();
        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        userRegistrationFacade.requestEvaluatorRegistration(request, user.getId());
        var response = userRegistrationFacade.downloadEvaluatorResume(user.getEmail(), admin.getId());
        assertNotNull(response);
    }

}
