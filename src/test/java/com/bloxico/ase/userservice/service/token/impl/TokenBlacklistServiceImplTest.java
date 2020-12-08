package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

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
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = UUID.randomUUID().toString();
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        service.blacklistTokens(oTokens, principalId);
        assertTrue(service.blacklistedTokens().contains(token));
        var tokens = repository.findDistinctTokenValues();
        assertEquals(List.of(token), tokens);
    }

    @Test
    public void blacklistTokens_sameTokenMultipleTimes() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = UUID.randomUUID().toString();
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        service.blacklistTokens(oTokens, principalId);
        assertTrue(service.blacklistedTokens().contains(token));
        assertEquals(
                List.of(token),
                repository.findDistinctTokenValues());
        service.blacklistTokens(oTokens, principalId);
        assertEquals(
                List.of(token, token),
                repository
                        .findAll()
                        .stream()
                        .map(BlacklistedToken::getToken)
                        .collect(toList()));
    }

    @Test // test with Thread.sleep(...) in blacklistedTokens()
    public void blacklistedTokens_caching() {
        assertTrue(service.blacklistedTokens().isEmpty());
        assertTrue(service.blacklistedTokens().isEmpty());
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = UUID.randomUUID().toString();
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        service.blacklistTokens(oTokens, principalId); // evicts cache
        assertFalse(service.blacklistedTokens().isEmpty());
        assertFalse(service.blacklistedTokens().isEmpty());
    }

}
