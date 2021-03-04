package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
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
    @Autowired private UtilSecurityContext utilSecurityContext;

    @Test
    @WithMockCustomUser
    public void blacklistedTokens_caching() {
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken));
        assertThat(service.blacklistedTokens(), hasItems(token));
    }

    @Test
    @WithMockCustomUser
    public void blacklistTokens_nullTokens() {
        assertThrows(
                NullPointerException.class,
                () -> service.blacklistTokens(null));
    }

    @Test
    @WithMockCustomUser
    public void blacklistTokens_emptyTokens() {
        service.blacklistTokens(List.of()); // refresh cache
        var size = service.blacklistedTokens().size();
        service.blacklistTokens(List.of());
        assertEquals(size, service.blacklistedTokens().size());
    }

    @Test
    @WithMockCustomUser
    public void blacklistTokens_generatedTokens() {
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken));
        assertThat(
                service.blacklistedTokens(),
                hasItems(token));
        assertThat(
                repository.findDistinctTokenValues(),
                hasItems(token));
    }

    @Test
    @WithMockCustomUser
    public void blacklistTokens_sameTokenMultipleTimes() {
        var user = utilUser.savedUser();
        var token = genUUID();
        var oToken = utilToken.toOAuthAccessTokenDto(user, token);
        service.blacklistTokens(List.of(oToken));
        assertThat(
                service.blacklistedTokens(),
                hasItems(token));
        assertThat(
                repository.findDistinctTokenValues(),
                hasItems(token));
        service.blacklistTokens(List.of(oToken));
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
    @WithMockCustomUser
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
    @WithMockCustomUser
    public void deleteExpiredTokens() {
        var principal = utilSecurityContext.getLoggedInPrincipal();
        var valid = utilToken.savedOauthTokenDto(principal.getEmail());
        var expired = utilToken.savedExpiredOauthTokenDto(principal.getEmail());
        assertThat(
                service.blacklistedTokens(),
                not(hasItems(
                        valid.getTokenId(),
                        expired.getTokenId())));
        service.blacklistTokens(List.of(valid, expired));
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
