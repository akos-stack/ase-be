package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractTokenServiceImplTest extends AbstractSpringTest {

    protected abstract Token.Type tokenType();

    protected abstract AbstractTokenServiceImpl tokenService();

    @Autowired private UtilUser utilUser;
    @Autowired private UtilToken utilToken;
    @Autowired private TokenRepository tokenRepository;

    @Test
    public void createTokenForUser() {
        var userId = utilUser.savedAdmin().getId();
        assertThat(tokenRepository
                        .findAll()
                        .stream()
                        .map(Token::getUserId)
                        .collect(toList()),
                not(hasItems(userId)));
        var value = tokenService().createTokenForUser(userId).getValue();
        var token = tokenRepository.findByValue(value).orElseThrow();
        assertEquals(userId, token.getUserId());
    }

    @Test
    public void refreshToken_nullTokenValue() {
        assertThrows(
                NullPointerException.class,
                () -> tokenService().refreshToken(null));
    }

    @Test
    public void refreshToken_tokenNotFound() {
        var token = genUUID();
        assertThrows(
                TokenException.class,
                () -> tokenService().refreshToken(token));
    }

    @Test
    public void refreshToken() {
        var userId = utilUser.savedAdmin().getId();
        var oldToken = tokenService().createTokenForUser(userId);
        var updToken = tokenService().refreshToken(oldToken.getValue());
        assertNotEquals(oldToken.getValue(), updToken.getValue());
        assertTrue(updToken.getExpiryDate().isAfter(oldToken.getExpiryDate()));
        assertEquals(updToken, tokenService().getTokenByUserId(userId));
    }

    @Test
    public void consumeToken_nullTokenValue() {
        assertThrows(
                NullPointerException.class,
                () -> tokenService().consumeToken(null));
    }

    @Test
    public void consumeToken_tokenNotFound() {
        assertThrows(
                TokenException.class,
                () -> tokenService().consumeToken(genUUID()));
    }

    @Test
    public void consumeToken_expiredToken() {
        var token = utilToken.savedExpiredToken(tokenType()).getValue();
        assertThrows(
                TokenException.class,
                () -> tokenService().consumeToken(token));
    }

    @Test
    public void consumeToken() {
        var userId = utilUser.savedAdmin().getId();
        var token = tokenService().createTokenForUser(userId).getValue();
        assertTrue(tokenRepository.findByValue(token).isPresent());
        tokenService().consumeToken(token);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
    }

    @Test
    public void getOrCreateTokenForUser() {
        var userId = utilUser.savedAdmin().getId();
        var count1 = tokenRepository.findAll().size();
        var token1 = tokenService().getOrCreateTokenForUser(userId);
        var count2 = tokenRepository.findAll().size();
        assertEquals(count1 + 1, count2);
        var token2 = tokenService().getOrCreateTokenForUser(userId);
        var count3 = tokenRepository.findAll().size();
        assertEquals(count2, count3);
        assertEquals(token1, token2); // doesn't check user_id
        assertEquals(token1.getUserId(), token2.getUserId());
    }

    @Test
    public void getOrCreateTokenForUser_expiredToken() {
        var token1 = utilToken.savedExpiredTokenDto(tokenType());
        var userId = token1.getUserId();
        var count1 = tokenRepository.findAll().size();
        var token2 = tokenService().getOrCreateTokenForUser(userId);
        var count2 = tokenRepository.findAll().size();
        assertEquals(count1 + 1, count2);
        assertNotEquals(token1, token2);
        assertEquals(token1.getUserId(), token2.getUserId());
    }

    @Test
    public void getTokenByUserId_tokenNotFound() {
        var userId = utilUser.savedAdmin().getId();
        assertThrows(
                TokenException.class,
                () -> tokenService().getTokenByUserId(userId));
    }

    @Test
    public void getTokenByUserId_expiredToken() {
        var userId = utilToken.savedExpiredToken(tokenType()).getUserId();
        assertNotNull(tokenService().getTokenByUserId(userId));
    }

    @Test
    public void getTokenByUserId() {
        var userId = utilToken.savedToken(tokenType()).getUserId();
        assertNotNull(tokenService().getTokenByUserId(userId));
    }

    @Test
    public void deleteExpiredTokens() {
        var valid = utilToken.savedToken(tokenType());
        var expired = utilToken.savedExpiredToken(tokenType());
        assertThat(
                tokenRepository.findAll(),
                hasItems(valid, expired));
        assertThat(
                tokenService().deleteExpiredTokens(),
                hasItems(expired.getUserId()));
        assertThat(
                tokenRepository.findAll(),
                allOf(hasItems(valid), not(hasItems(expired))));
    }

}
