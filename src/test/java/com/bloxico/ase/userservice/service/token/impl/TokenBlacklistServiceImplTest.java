package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenBlacklistServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenBlacklistServiceImpl service;

    @Autowired
    private BlacklistedTokenRepository repository;

    @Test // test with Thread.sleep(...) in blacklistedTokens()
    public void blacklistedTokens_caching() {
        assertTrue(service.blacklistedTokens().isEmpty());
        assertTrue(service.blacklistedTokens().isEmpty());
        var principalId = mockUtil.savedAdmin().getId();
        var userProfile = mockUtil.savedUserProfile();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(userProfile, token);
        service.blacklistTokens(List.of(oToken), principalId); // evicts cache
        assertFalse(service.blacklistedTokens().isEmpty());
        assertFalse(service.blacklistedTokens().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void blacklistTokens_nullTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        service.blacklistTokens(null, principalId);
    }

    @Test
    public void blacklistTokens_emptyTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        service.blacklistTokens(List.of(), principalId);
        assertTrue(service.blacklistedTokens().isEmpty());
    }

    @Test
    public void blacklistTokens_generatedTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfile = mockUtil.savedUserProfile();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(userProfile, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertTrue(service.blacklistedTokens().contains(token));
        var tokens = repository.findDistinctTokenValues();
        assertEquals(List.of(token), tokens);
    }

    @Test
    public void blacklistTokens_sameTokenMultipleTimes() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfile = mockUtil.savedUserProfile();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(userProfile, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertTrue(service.blacklistedTokens().contains(token));
        assertEquals(
                List.of(token),
                repository.findDistinctTokenValues());
        service.blacklistTokens(List.of(oToken), principalId);
        assertEquals(
                List.of(token, token),
                repository
                        .findAll()
                        .stream()
                        .map(BlacklistedToken::getToken)
                        .collect(toList()));
    }

    @Test(expected = NullPointerException.class)
    public void checkIfBlacklisted_nullToken() {
        service.checkIfBlacklisted(null);
    }

    @Test(expected = TokenException.class)
    public void checkIfBlacklisted_blacklistedToken() {
        var token = mockUtil.savedBlacklistedToken();
        service.checkIfBlacklisted(token.getToken());
    }

    @Test
    public void checkIfBlacklisted_nonBlacklistedToken() {
        service.checkIfBlacklisted(uuid());
    }

    @Test
    public void deleteExpiredTokens() {
        var principal = mockUtil.savedAdmin();
        var valid = mockUtil.savedOauthTokenDto(principal.getEmail());
        var expired = mockUtil.savedExpiredOauthTokenDto(principal.getEmail());
        assertEquals(
                Set.of(),
                service.blacklistedTokens());
        service.blacklistTokens(List.of(valid, expired), principal.getId());
        assertEquals(
                Set.of(valid.getTokenId(), expired.getTokenId()),
                service.blacklistedTokens());
        service.deleteExpiredTokens();
        assertEquals(
                Set.of(valid.getTokenId()),
                service.blacklistedTokens());
    }

}
