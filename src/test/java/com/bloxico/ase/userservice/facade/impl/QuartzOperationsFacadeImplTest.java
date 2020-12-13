package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static org.junit.Assert.assertEquals;

public class QuartzOperationsFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private QuartzOperationsFacadeImpl quartzOperationsFacade;

    @Test
    public void deleteExpiredTokens() {

        var validToken = mockUtil.savedToken(PASSWORD_RESET);
        var validOAuthAccessToken = mockUtil.savedOauthToken(uuid());
        var validBlacklistedToken = mockUtil.savedBlacklistedToken();

        var expiredToken = mockUtil.savedExpiredToken(REGISTRATION);
        var expiredOAuthAccessToken = mockUtil.savedExpiredOauthToken(uuid());
        var expiredBlacklistedToken = mockUtil.savedExpiredBlacklistedToken();

        assertEquals(
                Set.of(validToken, expiredToken),
                Set.copyOf(tokenRepository.findAll()));
        assertEquals(
                Set.of(validOAuthAccessToken, expiredOAuthAccessToken),
                Set.copyOf(oAuthAccessTokenRepository.findAll()));
        assertEquals(
                Set.of(validBlacklistedToken, expiredBlacklistedToken),
                Set.copyOf(blacklistedTokenRepository.findAll()));

        quartzOperationsFacade.deleteExpiredTokens();

        assertEquals(
                Set.of(validToken),
                Set.copyOf(tokenRepository.findAll()));
        assertEquals(
                Set.of(validOAuthAccessToken),
                Set.copyOf(oAuthAccessTokenRepository.findAll()));
        assertEquals(
                Set.of(validBlacklistedToken),
                Set.copyOf(blacklistedTokenRepository.findAll()));
    }

}
