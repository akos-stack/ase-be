package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorRegistrationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PendingEvaluatorServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilToken utilToken;
    @Autowired private PendingEvaluatorRepository repository;
    @Autowired private PendingEvaluatorServiceImpl service;
    @Autowired private UtilSecurityContext utilSecurityContext;

    @Test
    @WithMockCustomUser
    public void createPendingEvaluator_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> service.createPendingEvaluator(null));
    }

    @Test
    @WithMockCustomUser
    public void createPendingEvaluator_evaluatorAlreadyInvited() {
        var user = utilUser.savedUser();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request)));
    }

    @Test
    @WithMockCustomUser(role = Role.USER)
    public void createPendingEvaluator_evaluatorAlreadyRegistered() {
        var user = utilSecurityContext.getLoggedInPrincipal();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request)));
    }

    @Test
    @WithMockCustomUser
    public void createPendingEvaluator_evaluatorInvited() {
        var user = utilUser.savedUser();
        var admin = utilSecurityContext.getLoggedInPrincipal();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var pendingEvaluatorDto = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertNotNull(pendingEvaluatorDto);

        var newPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElseThrow();

        assertEquals(pendingEvaluatorDto.getToken(), newPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newPendingEvaluator.getStatus());

        assertSame(INVITED, newPendingEvaluator.getStatus());
    }

    @Test
    @WithMockCustomUser(role = Role.USER)
    public void createPendingEvaluator_evaluatorRequested() {
        var user = utilSecurityContext.getLoggedInPrincipal();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        var pendingEvaluatorDto = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertNotNull(pendingEvaluatorDto);

        var newlyCreatedPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElseThrow();

        assertEquals(pendingEvaluatorDto.getToken(), newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newlyCreatedPendingEvaluator.getStatus());

        assertSame(REQUESTED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    @WithMockCustomUser
    public void createPendingEvaluator_updateFromRequestedToInvited() {
        var user = utilUser.savedUser();
        var admin = utilSecurityContext.getLoggedInPrincipal();

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(registrationRequest));

        var pendingEvaluator = repository
                .findByEmailIgnoreCase(user.getEmail())
                .orElseThrow();

        assertNull(pendingEvaluator.getUpdaterId());
        assertSame(REQUESTED, pendingEvaluator.getStatus());

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(invitationRequest));

        assertNotNull(pendingEvaluator);
        assertEquals(admin.getId(), pendingEvaluator.getUpdaterId());
        assertSame(INVITED, pendingEvaluator.getStatus());
    }

    @Test
    @WithMockCustomUser
    public void createPendingEvaluator_updateFromInvitedToRequested() {
        var user = utilUser.savedUser();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(invitationRequest));

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(registrationRequest)));
    }

    @Test
    public void getPendingEvaluatorToken_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.getPendingEvaluatorToken(null));
    }

    @Test
    public void getPendingEvaluatorToken_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> service.getPendingEvaluatorToken(genUUID()));
    }

    @Test
    @WithMockCustomUser
    public void getPendingEvaluatorToken_withInvitedStatus() {
        var user = utilUser.savedUser();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var pendingEvaluator = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertNotNull(pendingEvaluator);
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        var newlyCreatedPendingEvaluatorToken = service.getPendingEvaluatorToken(user.getEmail());

        assertNotNull(newlyCreatedPendingEvaluatorToken);
        assertEquals(pendingEvaluator.getToken(), newlyCreatedPendingEvaluatorToken);
    }

    @Test
    @WithMockCustomUser
    public void getPendingEvaluatorToken_withRequestedStatus() {
        var user = utilUser.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        var pendingEvaluator = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertNotNull(pendingEvaluator);
        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));

        var newlyCreatedPendingEvaluatorToken = service.getPendingEvaluatorToken(user.getEmail());

        assertNotNull(newlyCreatedPendingEvaluatorToken);
        assertEquals(pendingEvaluator.getToken(), newlyCreatedPendingEvaluatorToken);
    }

    @Test
    public void checkInvitationToken_nullToken() {
        assertThrows(
                NullPointerException.class,
                () -> service.checkInvitationToken(null));
    }

    @Test
    public void checkInvitationToken_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> service.checkInvitationToken(genUUID()));
    }

    @Test
    @WithMockCustomUser
    public void checkInvitationToken_invitationTokenNotFound() {
        var request = new EvaluatorRegistrationRequest(genEmail(), genMultipartFile(pdf));
        var pending = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));
        assertThrows(
                TokenException.class,
                () -> service.checkInvitationToken(pending.getToken()));
    }

    @Test
    @WithMockCustomUser
    public void checkInvitationToken() {
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));
        service.checkInvitationToken(pending.getToken());
    }

    @Test
    public void deletePendingEvaluator_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.deletePendingEvaluator(null));
    }

    @Test
    public void deletePendingEvaluator_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> service.deletePendingEvaluator(genUUID()));
    }

    @Test
    @WithMockCustomUser
    public void deletePendingEvaluator_whenInInvitedStatus() {
        var user = utilUser.savedUser();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));
        service.deletePendingEvaluator(user.getEmail());
        assertFalse(utilToken.isEvaluatorPending(user.getEmail()));
    }

    @Test
    @WithMockCustomUser
    public void deletePendingEvaluator_whenInRequestedStatus() {
        var user = utilUser.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));
        service.deletePendingEvaluator(user.getEmail());
        assertFalse(utilToken.isEvaluatorPending(user.getEmail()));
    }

    @Test
    public void consumePendingEvaluator_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.consumePendingEvaluator(null, genUUID()));
    }

    @Test
    public void consumePendingEvaluator_nullToken() {
        assertThrows(
                NullPointerException.class,
                () -> service.consumePendingEvaluator(genUUID(), null));
    }

    @Test
    public void consumePendingEvaluator_evaluatorNotFound() {
        assertThrows(
                TokenException.class,
                () -> service.consumePendingEvaluator(genUUID(), genUUID()));
    }

    @Test
    @WithMockCustomUser
    public void consumePendingEvaluator() {
        var user = utilUser.savedUser();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var token = service
                .createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request))
                .getToken();

        assertTrue(utilToken.isEvaluatorPending(user.getEmail()));
        service.consumePendingEvaluator(user.getEmail(), token);
        assertFalse(utilToken.isEvaluatorPending(user.getEmail()));
    }

    @Test
    public void searchPendingEvaluators_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.searchPendingEvaluators(null, 0, 10, "email"));
    }

    @Test
    public void searchPendingEvaluators_pageIndexLessThanZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPendingEvaluators(genEmail(), -1, 10, "email"));
    }

    @Test
    public void searchPendingEvaluators_pageSizeLessThanOne() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPendingEvaluators(genEmail(), 0, 0, "email"));
    }

    @Test
    public void searchPendingEvaluators_nullSortCriteria() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPendingEvaluators(genEmail(), 0, 10, null));
    }

    @Test
    public void searchPendingEvaluators_emptySortCriteria() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPendingEvaluators(genEmail(), 0, 10, ""));
    }

    @Test
    public void searchPendingEvaluators_sortCriteriaDoesNotMatchAnyProperty() {
        assertThrows(
                PropertyReferenceException.class,
                () -> service.searchPendingEvaluators(genEmail(), 0, 10, genUUID()));
    }

    @Test
    @WithMockCustomUser
    public void searchPendingEvaluators_emptyResultSet() {
        utilToken.savedInvitedPendingEvaluatorDto();
        var list = service.searchPendingEvaluators(genEmail(), 0, 10, "email");
        assertEquals(0, list.getContent().size());
    }

    @Test
    @WithMockCustomUser
    public void searchPendingEvaluators() {
        var p1 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var p2 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var p3 = utilToken.savedInvitedPendingEvaluatorDto(genEmail("barFoo"));
        assertThat(
                service.searchPendingEvaluators("fooBar", 0, MAX_VALUE, "email").getContent(),
                allOf(
                        hasItems(p1, p2),
                        not(hasItems(p3))));
    }

    @Test
    @WithMockCustomUser
    public void getEvaluatorResume_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.getEvaluatorResume(null));
    }

    @Test
    @WithMockCustomUser
    public void getEvaluatorResume_resumeNotFound() {
        assertThrows(
                UserException.class,
                () -> service.getEvaluatorResume(genEmail()));
    }

    @Test
    @WithMockCustomUser
    public void getEvaluatorResume() {
        var pendingEvaluatorEmail = utilToken.savedRequestedPendingEvaluatorDto();
        var resume = service.getEvaluatorResume(pendingEvaluatorEmail);
        assertNotNull(resume);
        assertEquals(resume.getEmail(), pendingEvaluatorEmail);
    }

}
