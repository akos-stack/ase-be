package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorRegistrationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;

import static com.bloxico.ase.testutil.MockUtil.*;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PendingEvaluatorServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PendingEvaluatorRepository repository;

    @Autowired
    private PendingEvaluatorServiceImpl service;

    @Test
    public void createPendingEvaluator_nullRequest() {
        var admin = mockUtil.savedAdmin();
        assertThrows(
                NullPointerException.class,
                () -> service.createPendingEvaluator(null, admin.getId()));
    }

    @Test
    public void createPendingEvaluator_evaluatorAlreadyInvited() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(request, admin.getId()));
    }

    @Test
    public void createPendingEvaluator_evaluatorAlreadyRegistered() {
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(request, user.getId()));
    }

    @Test
    public void createPendingEvaluator_evaluatorInvited() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var pendingEvaluatorDto = service.createPendingEvaluator(request, admin.getId());

        assertNotNull(pendingEvaluatorDto);

        var newPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElseThrow();

        assertEquals(pendingEvaluatorDto.getToken(), newPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newPendingEvaluator.getStatus());

        assertNull(newPendingEvaluator.getCvPath());
        assertSame(INVITED, newPendingEvaluator.getStatus());
    }

    @Test
    public void createPendingEvaluator_evaluatorRequested() {
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        var pendingEvaluatorDto = service.createPendingEvaluator(request, user.getId());

        assertNotNull(pendingEvaluatorDto);

        var newlyCreatedPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElseThrow();

        assertEquals(pendingEvaluatorDto.getToken(), newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newlyCreatedPendingEvaluator.getStatus());

        assertNotNull(newlyCreatedPendingEvaluator.getCvPath());
        assertSame(REQUESTED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    public void createPendingEvaluator_updateFromRequestedToInvited() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(registrationRequest, user.getId());

        var pendingEvaluator = repository
                .findByEmailIgnoreCase(user.getEmail())
                .orElseThrow();

        assertNull(pendingEvaluator.getUpdaterId());
        assertSame(REQUESTED, pendingEvaluator.getStatus());

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(invitationRequest, admin.getId());

        assertNotNull(pendingEvaluator);
        assertEquals(admin.getId(), pendingEvaluator.getUpdaterId());
        assertSame(INVITED, pendingEvaluator.getStatus());
    }

    @Test
    public void createPendingEvaluator_updateFromInvitedToRequested() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(invitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        assertThrows(
                TokenException.class,
                () -> service.createPendingEvaluator(registrationRequest, user.getId()));
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
                () -> service.getPendingEvaluatorToken(uuid()));
    }

    @Test
    public void getPendingEvaluatorToken_withInvitedStatus() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var pendingEvaluator = service.createPendingEvaluator(request, admin.getId());

        assertNotNull(pendingEvaluator);
        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var newlyCreatedPendingEvaluatorToken = service.getPendingEvaluatorToken(user.getEmail());

        assertNotNull(newlyCreatedPendingEvaluatorToken);
        assertEquals(pendingEvaluator.getToken(), newlyCreatedPendingEvaluatorToken);
    }

    @Test
    public void getPendingEvaluatorToken_withRequestedStatus() {
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        var pendingEvaluator = service.createPendingEvaluator(request, user.getId());

        assertNotNull(pendingEvaluator);
        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

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
                () -> service.checkInvitationToken(uuid()));
    }

    @Test
    public void checkInvitationToken_invitationTokenNotFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorRegistrationRequest(genEmail(), genMultipartFile(pdf));
        var pending = service.createPendingEvaluator(request, principalId);
        assertThrows(
                TokenException.class,
                () -> service.checkInvitationToken(pending.getToken()));
    }

    @Test
    public void checkInvitationToken() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = service.createPendingEvaluator(request, principalId);
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
                () -> service.deletePendingEvaluator(uuid()));
    }

    @Test
    public void deletePendingEvaluator_whenInInvitedStatus() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
        service.deletePendingEvaluator(user.getEmail());
        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test
    public void deletePendingEvaluator_whenInRequestedStatus() {
        var user = mockUtil.savedUser();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
        service.deletePendingEvaluator(user.getEmail());
        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test
    public void consumePendingEvaluator_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.consumePendingEvaluator(null, uuid()));
    }

    @Test
    public void consumePendingEvaluator_nullToken() {
        assertThrows(
                NullPointerException.class,
                () -> service.consumePendingEvaluator(uuid(), null));
    }

    @Test
    public void consumePendingEvaluator_evaluatorNotFound() {
        assertThrows(
                TokenException.class,
                () -> service.consumePendingEvaluator(uuid(), uuid()));
    }

    @Test
    public void consumePendingEvaluator() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var token = service
                .createPendingEvaluator(request, admin.getId())
                .getToken();

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
        service.consumePendingEvaluator(user.getEmail(), token);
        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test
    public void searchPendingEvaluators_nullEmail() {
        assertThrows(
                InvalidDataAccessApiUsageException.class,
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
                () -> service.searchPendingEvaluators(genEmail(), 0, 10, uuid()));
    }

    @Test
    public void searchPendingEvaluators_emptyResultSet() {
        mockUtil.savedInvitedPendingEvaluatorDto();
        var list = service.searchPendingEvaluators(genEmail(), 0, 10, "email");
        assertEquals(0, list.getContent().size());
    }

    @Test
    public void searchPendingEvaluators() {
        var p1 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var p2 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("fooBar"));
        var p3 = mockUtil.savedInvitedPendingEvaluatorDto(genEmail("barFoo"));
        assertThat(
                service.searchPendingEvaluators("fooBar", 0, MAX_VALUE, "email").getContent(),
                allOf(
                        hasItems(p1, p2),
                        not(hasItems(p3))));
    }

    // TODO-TEST getEvaluatorResume_nullEmail

    // TODO-TEST getEvaluatorResume_tokenNotFound

    // TODO-TEST getEvaluatorResume_resumeNotFound

    @Test
    public void getEvaluatorResume() {
        var user = mockUtil.savedUser();
        var admin = mockUtil.savedAdmin();
        var request = new EvaluatorRegistrationRequest(user.getEmail(), genMultipartFile(pdf));
        service.createPendingEvaluator(request, user.getId());
        assertNotNull(service.getEvaluatorResume(user.getEmail(), admin.getId()));
    }

}
