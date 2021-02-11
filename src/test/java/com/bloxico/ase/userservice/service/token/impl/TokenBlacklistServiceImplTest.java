package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenBlacklistServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilToken utilToken;
    @Autowired private TokenBlacklistServiceImpl service;
    @Autowired private BlacklistedTokenRepository repository;

    @Test
    public void blacklistedTokens_caching() {
        var principalId = utilUser.savedAdmin().getId();
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken), principalId);
        assertThat(service.blacklistedTokens(), hasItems(token));
    }

    @Test
    public void blacklistTokens_nullTokens() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.blacklistTokens(null, principalId));
    }

    @Test
    public void blacklistTokens_emptyTokens() {
        var principalId = utilUser.savedAdmin().getId();
        service.blacklistTokens(List.of(), principalId); // refresh cache
        var size = service.blacklistedTokens().size();
        service.blacklistTokens(List.of(), principalId);
        assertEquals(size, service.blacklistedTokens().size());
    }

    @Test
    public void blacklistTokens_generatedTokens() {
        var principalId = utilUser.savedAdmin().getId();
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
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
        var principalId = utilUser.savedAdmin().getId();
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
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
        var token = utilToken.savedBlacklistedToken();
        assertThrows(
                TokenException.class,
                () -> service.checkIfBlacklisted(token.getValue()));
    }

    @Test
    public void checkIfBlacklisted_nonBlacklistedToken() {
        service.checkIfBlacklisted(genUUID());
    }

    @Test
    public void deleteExpiredTokens() {
        var principal = utilUser.savedAdmin();
        var valid = utilToken.savedOauthTokenDto(principal.getEmail());
        var expired = utilToken.savedExpiredOauthTokenDto(principal.getEmail());
        assertThat(
                service.blacklistedTokens(),
                not(hasItems(
                        valid.getTokenId(),
                        expired.getTokenId())));
        service.blacklistTokens(List.of(valid, expired), principal.getId());
        assertThat(
                service.blacklistedTokens(),
                hasItems(
                        valid.getTokenId(),
                        expired.getTokenId()));
        service.deleteExpiredTokens();
        assertThat(
                service.blacklistedTokens(),
                hasItems(valid.getTokenId()));
    }

}
