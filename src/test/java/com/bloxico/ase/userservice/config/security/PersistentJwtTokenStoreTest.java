package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class PersistentJwtTokenStoreTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    @Test
    public void storeAccessToken_multipleTokensForTheSameUser() {
        var registration = mockUtil.doConfirmedRegistration();
        var token1 = mockUtil.doAuthentication(registration);
        var token2 = mockUtil.doAuthentication(registration);
        var token3 = mockUtil.doAuthentication(registration);
        var dbTokens = oAuthAccessTokenRepository
                .findAll()
                .stream()
                .map(OAuthAccessToken::getTokenId)
                .map(token -> "Bearer " + token)
                .collect(toSet());
        assertEquals(dbTokens, Set.of(token1, token2, token3));
    }

}
