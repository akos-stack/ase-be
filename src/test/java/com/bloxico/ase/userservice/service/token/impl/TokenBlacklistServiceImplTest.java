package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenBlacklistServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenBlacklistServiceImpl service;

    @Autowired
    private BlacklistedTokenRepository repository;

    @Test
    public void blacklistedTokens_caching() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUser();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertThat(service.blacklistedTokens(), hasItems(token));
    }

    @Test
    public void blacklistTokens_nullTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.blacklistTokens(null, principalId));
    }

    @Test
    public void blacklistTokens_emptyTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var size = service.blacklistedTokens().size();
        service.blacklistTokens(List.of(), principalId);
        assertEquals(size, service.blacklistedTokens().size());
    }

    @Test
    public void blacklistTokens_generatedTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUser();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertThat(
                service.blacklistedTokens(),
                hasItems(token));
        assertThat(
                repository.findDistinctTokenValues(),
                hasItems(token));
    }

    @Test
    public void blacklistTokens_sameTokenMultipleTimes() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUser();
        var token = uuid();
        var oToken = mockUtil.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertThat(
                service.blacklistedTokens(),
                hasItems(token));
        assertThat(
                repository.findDistinctTokenValues(),
                hasItems(token));
        service.blacklistTokens(List.of(oToken), principalId);
        assertEquals(
                2,
                repository
                        .findAll()
                        .stream()
                        .map(BlacklistedToken::getValue)
                        .filter(token::equals)
                        .count());
    }

    @Test
    public void checkIfBlacklisted_nullToken() {
        assertThrows(
                NullPointerException.class,
                () -> service.checkIfBlacklisted(null));
    }

    @Test
    public void checkIfBlacklisted_blacklistedToken() {
        var token = mockUtil.savedBlacklistedToken();
        assertThrows(
                TokenException.class,
                () -> service.checkIfBlacklisted(token.getValue()));
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
        assertThat(
                service.blacklistedTokens(),
                not(hasItems(
                        valid.getTokenId(),
                        expired.getTokenId())));
        service.blacklistTokens(List.of(valid, expired), principal.getId());
        assertEquals(
                service.blacklistedTokens(),
                hasItems(valid.getTokenId(),
                        expired.getTokenId()));
        service.deleteExpiredTokens();
        assertEquals(
                service.blacklistedTokens(),
                hasItems(valid.getTokenId()));
    }

}
