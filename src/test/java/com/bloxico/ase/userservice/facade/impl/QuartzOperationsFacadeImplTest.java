package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
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
    private UserRepository userRepository;

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

        assertThat(
                tokenRepository.findAll(),
                hasItems(
                        validRegistrationToken,
                        validPasswordResetToken,
                        expiredRegistrationToken,
                        expiredPasswordResetToken));

        assertThat(
                oAuthAccessTokenRepository.findAll(),
                hasItems(
                        validOAuthAccessToken,
                        expiredOAuthAccessToken));

        assertThat(
                blacklistedTokenRepository.findAll(),
                hasItems(
                        validBlacklistedToken,
                        expiredBlacklistedToken));

        assertTrue(userRepository.findById(expiredRegistrationToken.getUserId()).isPresent());

        quartzOperationsFacade.deleteExpiredTokens();

        assertThat(
                tokenRepository.findAll(),
                hasItems(
                        validRegistrationToken,
                        validPasswordResetToken));

        assertThat(
                oAuthAccessTokenRepository.findAll(),
                hasItems(validOAuthAccessToken));

        assertThat(
                blacklistedTokenRepository.findAll(),
                hasItems(validBlacklistedToken));

        entityManager.clear(); // needs to be cleared because of deleteInBatch
        assertTrue(userRepository.findById(expiredRegistrationToken.getUserId()).isEmpty());
    }

}
