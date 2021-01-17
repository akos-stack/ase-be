package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorRegistrationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;

import java.util.Comparator;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.MockUtil.genEmail;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static org.junit.Assert.*;

public class PendingEvaluatorServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PendingEvaluatorRepository repository;

    @Autowired
    private PendingEvaluatorServiceImpl service;

    @Test(expected = NullPointerException.class)
    public void createPendingEvaluator_requestIsNull() {
        var admin = mockUtil.savedAdmin();

        service.createPendingEvaluator(null, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void createPendingEvaluator_evaluatorAlreadyPendingAfterInvitation() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.createPendingEvaluator(request, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void createPendingEvaluator_evaluatorAlreadyPendingAfterRegistration() {
        var user = mockUtil.savedUserProfile();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        service.createPendingEvaluator(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.createPendingEvaluator(request, user.getId());
    }

    @Test
    public void createPendingEvaluator_evaluatorIsInvited() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var pendingEvaluatorDto = service.createPendingEvaluator(request, admin.getId());

        assertNotNull(pendingEvaluatorDto);

        var newlyCreatedPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertEquals(pendingEvaluatorDto.getToken(), newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newlyCreatedPendingEvaluator.getStatus());

        assertNull(newlyCreatedPendingEvaluator.getCvPath());
        assertSame(INVITED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    public void createPendingEvaluator_evaluatorIsRequested() {
        var user = mockUtil.savedUserProfile();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        var pendingEvaluatorDto = service.createPendingEvaluator(request, user.getId());

        assertNotNull(pendingEvaluatorDto);

        var newlyCreatedPendingEvaluator = repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), pendingEvaluatorDto.getToken())
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertEquals(pendingEvaluatorDto.getToken(), newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(user.getId(), newlyCreatedPendingEvaluator.getCreatorId());
        assertEquals(pendingEvaluatorDto.getStatus(), newlyCreatedPendingEvaluator.getStatus());

        assertNotNull(newlyCreatedPendingEvaluator.getCvPath());
        assertSame(REQUESTED, newlyCreatedPendingEvaluator.getStatus());
    }

    @Test
    public void createPendingEvaluator_updateFromRequestedToInvited() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        service.createPendingEvaluator(registrationRequest, user.getId());

        var pendingEvaluator = repository
                .findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        assertNotNull(pendingEvaluator);
        assertNull(pendingEvaluator.getUpdaterId());
        assertSame(REQUESTED, pendingEvaluator.getStatus());

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(invitationRequest, admin.getId());

        assertNotNull(pendingEvaluator);
        assertEquals(admin.getId(), pendingEvaluator.getUpdaterId());
        assertSame(INVITED, pendingEvaluator.getStatus());
    }

    @Test(expected = TokenException.class)
    public void createPendingEvaluator_updateFromInvitedToRequested() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var invitationRequest = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(invitationRequest, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var registrationRequest = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        service.createPendingEvaluator(registrationRequest, user.getId());
    }

    @Test(expected = NullPointerException.class)
    public void getPendingEvaluatorToken_emailIsNull() {
        service.getPendingEvaluatorToken(null);
    }

    @Test(expected = TokenException.class)
    public void getPendingEvaluatorToken_tokenNotFound() {
        service.getPendingEvaluatorToken(uuid());
    }

    @Test
    public void getPendingEvaluatorToken_whenInInvitedStatus() {
        var user = mockUtil.savedUserProfile();
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
    public void getPendingEvaluatorToken_whenInRequestedStatus() {
        var user = mockUtil.savedUserProfile();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        var pendingEvaluator = service.createPendingEvaluator(request, user.getId());

        assertNotNull(pendingEvaluator);
        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        var newlyCreatedPendingEvaluatorToken = service.getPendingEvaluatorToken(user.getEmail());

        assertNotNull(newlyCreatedPendingEvaluatorToken);
        assertEquals(pendingEvaluator.getToken(), newlyCreatedPendingEvaluatorToken);
    }

    @Test(expected = NullPointerException.class)
    public void checkInvitationToken_nullToken() {
        service.checkInvitationToken(null);
    }

    @Test(expected = TokenException.class)
    public void checkInvitationToken_tokenNotFound() {
        service.checkInvitationToken(uuid());
    }

    @Test(expected = TokenException.class)
    public void checkInvitationToken_invitationTokenNotFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorRegistrationRequest(genEmail(), MockUtil.createMultipartFile());
        var pending = service.createPendingEvaluator(request, principalId);
        service.checkInvitationToken(pending.getToken());
    }

    @Test
    public void checkInvitationToken() {
        var principalId = mockUtil.savedAdmin().getId();
        var request = new EvaluatorInvitationRequest(genEmail());
        var pending = service.createPendingEvaluator(request, principalId);
        service.checkInvitationToken(pending.getToken());
    }

    @Test(expected = NullPointerException.class)
    public void deletePendingEvaluator_emailIsNull() {
        service.deletePendingEvaluator(null);
    }

    @Test(expected = TokenException.class)
    public void deletePendingEvaluator_tokenNotFound() {
        service.deletePendingEvaluator(uuid());
    }

    @Test
    public void deletePendingEvaluator_whenInInvitedStatus() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.deletePendingEvaluator(user.getEmail());

        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test
    public void deletePendingEvaluator_whenInRequestedStatus() {
        var user = mockUtil.savedUserProfile();

        var request = new EvaluatorRegistrationRequest(user.getEmail(), MockUtil.createMultipartFile());
        service.createPendingEvaluator(request, user.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.deletePendingEvaluator(user.getEmail());

        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void consumePendingEvaluator_emailIsNull() {
        service.consumePendingEvaluator(null, uuid());
    }

    @Test(expected = NullPointerException.class)
    public void consumePendingEvaluator_tokenIsNull() {
        service.consumePendingEvaluator(uuid(), null);
    }

    @Test(expected = TokenException.class)
    public void consumePendingEvaluator_evaluatorWithGivenEmailAndTokenNotFound() {
        service.consumePendingEvaluator(uuid(), uuid());
    }

    @Test
    public void consumePendingEvaluator() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        var token = service
                .createPendingEvaluator(request, admin.getId())
                .getToken();

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.consumePendingEvaluator(user.getEmail(), token);

        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void searchPendingEvaluators_emailIsNull() {
        service.searchPendingEvaluators(null, 0, 10, "email");
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchPendingEvaluators_pageIndexIsLessThanZero() {
        service.searchPendingEvaluators(uuid(), -1, 10, "email");
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchPendingEvaluators_pageSizeIsLessThanOne() {
        service.searchPendingEvaluators(uuid(), 0, 0, "email");
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchPendingEvaluators_sortCriteriaIsNull() {
        service.searchPendingEvaluators(uuid(), 0, 10, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchPendingEvaluators_sortCriteriaIsEmpty() {
        service.searchPendingEvaluators(uuid(), 0, 10, "");
    }

    @Test(expected = PropertyReferenceException.class)
    public void searchPendingEvaluators_sortCriteriaDoesNotMatchAnyProperty() {
        service.searchPendingEvaluators(uuid(), 0, 10, "missingProperty");
    }

    @Test
    public void searchPendingEvaluators_emptyResultSet() {
        mockUtil.createInvitedPendingEvaluators();

        var list = service.searchPendingEvaluators(uuid(), 0, 10, "email");

        assertEquals(0, list.size());
    }

    @Test
    public void searchPendingEvaluators() {
        var pendingEvaluators = mockUtil.createInvitedPendingEvaluators();

        var emailFilter = "aseUser";
        var pageIndex = 1;
        var pageSize = 4;

        var expectedList = pendingEvaluators
                .stream()
                .filter(p -> p.getEmail().contains(emailFilter))
                .sorted(Comparator.comparing(PendingEvaluatorDto::getEmail))
                .skip(pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        var actualList = service.searchPendingEvaluators(emailFilter, pageIndex, pageSize, "email");

        assertEquals(expectedList, actualList);
    }

}
