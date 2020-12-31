package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class PendingEvaluatorServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PendingEvaluatorRepository repository;

    @Autowired
    private PendingEvaluatorServiceImpl service;

    @Test(expected = NullPointerException.class)
    public void createPendingEvaluator_emailIsNull() {
        var admin = mockUtil.savedAdmin();

        service.createPendingEvaluator(null, admin.getId());
    }

    @Test(expected = TokenException.class)
    public void createPendingEvaluator_evaluatorAlreadyPending() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        service.createPendingEvaluator(user.getEmail(), admin.getId());

        assertTrue(mockUtil.evaluatorAlreadyPending(user.getEmail()));

        service.createPendingEvaluator(user.getEmail(), admin.getId());
    }

    @Test
    public void createPendingEvaluator() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var token = service.createPendingEvaluator(user.getEmail(), admin.getId());

        assertNotNull(token);

        var newlyCreatedPendingEvaluator= repository
                .findByEmailIgnoreCaseAndToken(user.getEmail(), token)
                .orElse(null);

        assertNotNull(newlyCreatedPendingEvaluator);
        assertEquals(token, newlyCreatedPendingEvaluator.getToken());
        assertEquals(user.getEmail(), newlyCreatedPendingEvaluator.getEmail());
        assertEquals(admin.getId(), newlyCreatedPendingEvaluator.getCreatorId());
    }

    @Test(expected = NullPointerException.class)
    public void getPendingEvaluatorToken_emailIsNull() {
        service.getPendingEvaluatorToken(null);
    }

    @Test(expected = TokenException.class)
    public void getPendingEvaluatorToken_tokenNotFound() {
        service.getPendingEvaluatorToken("userNotFound@mail.com");
    }

    @Test
    public void getPendingEvaluatorToken() {
        var user = mockUtil.savedUserProfile();
        var admin = mockUtil.savedAdmin();

        var token = service.createPendingEvaluator(user.getEmail(), admin.getId());

        assertNotNull(token);
        assertTrue(mockUtil.evaluatorAlreadyPending(user.getEmail()));

        var newlyCreatedPendingEvaluatorToken= service.getPendingEvaluatorToken(user.getEmail());

        assertNotNull(newlyCreatedPendingEvaluatorToken);
        assertEquals(token, newlyCreatedPendingEvaluatorToken);
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

        service.createPendingEvaluator(user.getEmail(), admin.getId());

        assertTrue(mockUtil.evaluatorAlreadyPending(user.getEmail()));

        service.deletePendingEvaluator(user.getEmail());

        assertFalse(mockUtil.evaluatorAlreadyPending(user.getEmail()));
    }
}
