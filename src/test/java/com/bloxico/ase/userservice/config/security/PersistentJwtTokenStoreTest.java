package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class PersistentJwtTokenStoreTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    @Test
    public void storeAccessToken_multipleTokensForTheSameUser() {
        var registration = utilAuth.doConfirmedRegistration();
        var token1 = utilAuth.doAuthentication(registration);
        var token2 = utilAuth.doAuthentication(registration);
        var token3 = utilAuth.doAuthentication(registration);
        var dbTokens = oAuthAccessTokenRepository
                .findAll()
                .stream()
                .map(OAuthAccessToken::getTokenId)
                .map(token -> "Bearer " + token)
                .collect(toSet());
        assertThat(dbTokens, hasItems(token1, token2, token3));
    }

}
