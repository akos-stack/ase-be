package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorRegistrationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        var request = new EvaluatorRegistrationRequest(user.getEmail(), "storage.com/cv-123.docx");
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

        var request = new EvaluatorRegistrationRequest(user.getEmail(), "storage.com/cv-123.docx");
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

    @Test(expected = NullPointerException.class)
    public void getPendingEvaluatorToken_emailIsNull() {
        service.getPendingEvaluatorToken(null);
    }

    @Test(expected = TokenException.class)
    public void getPendingEvaluatorToken_tokenNotFound() {
        service.getPendingEvaluatorToken(uuid());
    }

    @Test
    public void getPendingEvaluatorToken() {
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

    @Test(expected = NullPointerException.class)
    public void deletePendingEvaluator_emailIsNull() {
        service.deletePendingEvaluator(null);
    }

    @Test(expected = TokenException.class)
    public void deletePendingEvaluator_tokenNotFound() {
        service.deletePendingEvaluator("userNotFound@mail.com");
    }

    @Test
    public void deletePendingEvaluator() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var request = new EvaluatorInvitationRequest(user.getEmail());
        service.createPendingEvaluator(request, admin.getId());

        assertTrue(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));

        service.deletePendingEvaluator(user.getEmail());

        assertFalse(mockUtil.isEvaluatorAlreadyPending(user.getEmail()));
    }

}
