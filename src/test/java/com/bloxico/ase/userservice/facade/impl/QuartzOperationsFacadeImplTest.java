package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private UserProfileRepository userProfileRepository;

    @Autowired
    private QuartzOperationsFacadeImpl quartzOperationsFacade;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void deleteExpiredTokens() {

        var validRegistrationToken = mockUtil.savedToken(REGISTRATION);
        var validPasswordResetToken = mockUtil.savedToken(PASSWORD_RESET);
        var validOAuthAccessToken = mockUtil.savedOauthToken(uuid());
        var validBlacklistedToken = mockUtil.savedBlacklistedToken();

        var expiredRegistrationToken = mockUtil.savedExpiredToken(REGISTRATION);
        var expiredPasswordResetToken = mockUtil.savedExpiredToken(PASSWORD_RESET);
        var expiredOAuthAccessToken = mockUtil.savedExpiredOauthToken(uuid());
        var expiredBlacklistedToken = mockUtil.savedExpiredBlacklistedToken();

        mockUtil.disableUser(expiredRegistrationToken.getUserId());

        assertEquals(
                Set.of(validRegistrationToken, validPasswordResetToken,
                        expiredRegistrationToken, expiredPasswordResetToken),
                Set.copyOf(tokenRepository.findAll()));
        assertEquals(
                Set.of(validOAuthAccessToken, expiredOAuthAccessToken),
                Set.copyOf(oAuthAccessTokenRepository.findAll()));
        assertEquals(
                Set.of(validBlacklistedToken, expiredBlacklistedToken),
                Set.copyOf(blacklistedTokenRepository.findAll()));
        assertTrue(userProfileRepository.findById(expiredRegistrationToken.getUserId()).isPresent());

        quartzOperationsFacade.deleteExpiredTokens();

        assertEquals(
                List.of(validRegistrationToken, validPasswordResetToken),
                tokenRepository.findAll());
        assertEquals(
                List.of(validOAuthAccessToken),
                oAuthAccessTokenRepository.findAll());
        assertEquals(
                List.of(validBlacklistedToken),
                blacklistedTokenRepository.findAll());
        entityManager.clear(); // needs to be cleared because of deleteInBatch
        assertTrue(userProfileRepository.findById(expiredRegistrationToken.getUserId()).isEmpty());
    }

}
