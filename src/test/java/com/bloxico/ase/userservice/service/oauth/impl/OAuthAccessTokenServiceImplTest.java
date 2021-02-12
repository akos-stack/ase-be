package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilToken;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genEmail;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OAuthAccessTokenServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilToken utilToken;
    @Autowired private OAuthAccessTokenRepository repository;
    @Autowired private OAuthAccessTokenServiceImpl service;

    @Test
    public void deleteTokensByEmail_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.deleteTokensByEmail(null));
    }

    @Test
    public void deleteTokensByEmail_notFound() {
        assertTrue(service.deleteTokensByEmail(genEmail()).isEmpty());
    }

    @Test
    public void deleteTokensByEmail() {
        var email1 = genEmail();
        var email2 = genEmail();
        var token1 = utilToken.savedOauthTokenDto(email1);
        var token2 = utilToken.savedOauthTokenDto(email1);
        var token3 = utilToken.savedOauthTokenDto(email2);
        assertEquals(
                Set.of(token1, token2),
                Set.copyOf(service.deleteTokensByEmail(email1)));
        assertThat(
                repository.findAllByUserNameIgnoreCase(email1),
                not(hasItems(token1, token2)));
        assertThat(
                repository.findAllByUserNameIgnoreCase(email2),
                hasItems(token3));
    }

    @Test
    public void deleteExpiredTokens() {
        var email = genEmail();
        var valid = utilToken.savedOauthTokenDto(email);
        var expired = utilToken.savedExpiredOauthTokenDto(email);
        assertThat(
                repository.findAllByUserNameIgnoreCase(email),
                hasItems(valid, expired));
        service.deleteExpiredTokens();
        assertThat(
                repository.findAllByUserNameIgnoreCase(email),
                allOf(hasItems(valid), not(hasItems(expired))));
    }

}
