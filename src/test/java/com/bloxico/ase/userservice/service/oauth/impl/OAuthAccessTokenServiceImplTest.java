package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
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
        var email = uuid();
        assertTrue(service.deleteTokensByEmail(email).isEmpty());
    }

    @Test
    public void deleteTokensByEmail() {
        var email1 = "fooBar@mail.com";
        var email2 = "barFoo@mail.com";
        var token1 = mockUtil.savedOauthTokenDto(email1);
        var token2 = mockUtil.savedOauthTokenDto(email1);
        var token3 = mockUtil.savedOauthTokenDto(email2);
        assertEquals(
                Set.of(token1, token2),
                Set.copyOf(service.deleteTokensByEmail(email1)));
        assertEquals(List.of(), repository.findAllByUserNameIgnoreCase(email1));
        assertEquals(List.of(token3), repository.findAllByUserNameIgnoreCase(email2));
    }

    @Test
    public void deleteExpiredTokens() {
        var email = uuid();
        var valid = mockUtil.savedOauthTokenDto(email);
        var expired = mockUtil.savedExpiredOauthTokenDto(email);
        assertEquals(
                Set.of(valid, expired),
                Set.copyOf(repository.findAllByUserNameIgnoreCase(email)));
        service.deleteExpiredTokens();
        assertEquals(
                Set.of(valid),
                Set.copyOf(repository.findAllByUserNameIgnoreCase(email)));
    }

}
