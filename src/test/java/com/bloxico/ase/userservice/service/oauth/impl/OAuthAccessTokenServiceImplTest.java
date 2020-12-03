package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OAuthAccessTokenServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OAuthAccessTokenRepository repository;

    @Autowired
    private OAuthAccessTokenServiceImpl service;

    @Test(expected = NullPointerException.class)
    public void deleteTokensByEmail_nullEmail() {
        service.deleteTokensByEmail(null);
    }

    @Test
    public void deleteTokensByEmail_notFound() {
        var email = UUID.randomUUID().toString();
        assertTrue(service.deleteTokensByEmail(email).isEmpty());
    }

    @Test
    public void deleteTokensByEmail() {
        var email1 = "fooBar@mail.com";
        var email2 = "barFoo@mail.com";
        var size = 5;
        mockUtil.genSavedTokens(size, email1);
        mockUtil.genSavedTokens(size, email2);
        assertEquals(size, service.deleteTokensByEmail(email1).size());
        assertEquals(0, repository.findAllByUserNameIgnoreCase(email1).size());
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email2).size());
    }

}
