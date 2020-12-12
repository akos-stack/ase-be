package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.junit.Assert.*;

public abstract class AbstractTokenServiceImplTest extends AbstractSpringTest {

    protected abstract Token.Type tokenType();

    protected abstract AbstractTokenServiceImpl tokenService();

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void createTokenForUser() {
        var userId = mockUtil.savedAdmin().getId();
        assertFalse(tokenRepository
                .findAll()
                .stream()
                .map(Token::getUserId)
                .anyMatch(userId::equals));
        var value = tokenService().createTokenForUser(userId).getValue();
        var token = tokenRepository.findByValue(value).orElseThrow();
        assertEquals(userId, token.getUserId());
    }

    @Test(expected = NullPointerException.class)
    public void refreshToken_nullToken() {
        tokenService().refreshToken(null);
    }

    @Test(expected = TokenException.class)
    public void refreshToken_tokenNotFound() {
        var token = uuid();
        tokenService().refreshToken(token);
    }

    @Test
    public void refreshToken() {
        var userId = mockUtil.savedAdmin().getId();
        var oldToken = tokenService().createTokenForUser(userId);
        var updToken = tokenService().refreshToken(oldToken.getValue());
        assertNotEquals(oldToken.getValue(), updToken.getValue());
        assertTrue(updToken.getExpiryDate().isAfter(oldToken.getExpiryDate()));
        assertEquals(updToken, tokenService().getTokenByUserId(userId));
    }

    @Test(expected = NullPointerException.class)
    public void consumeTokenForUser_nullTokenValue() {
        tokenService().consumeTokenForUser(null, -5);
    }

    @Test(expected = TokenException.class)
    public void consumeTokenForUser_tokenNotFound() {
        var token = uuid();
        tokenService().consumeTokenForUser(token, -5);
    }

    @Test(expected = TokenException.class)
    public void consumeTokenForUser_tokenOfAnotherUser() {
        var userId = mockUtil.savedAdmin().getId();
        var token = tokenService().createTokenForUser(userId).getValue();
        tokenService().consumeTokenForUser(token, -5);
    }

    @Test(expected = TokenException.class)
    public void consumeTokenForUser_expiredToken() {
        var token = mockUtil.savedExpiredToken(tokenType()).getValue();
        tokenService().consumeTokenForUser(token, -5);
    }

    @Test
    public void consumeTokenForUser() {
        var userId = mockUtil.savedAdmin().getId();
        var token = tokenService().createTokenForUser(userId).getValue();
        assertTrue(tokenRepository.findByValue(token).isPresent());
        tokenService().consumeTokenForUser(token, userId);
        assertTrue(tokenRepository.findByValue(token).isEmpty());
    }

    @Test
    public void getOrCreateTokenForUser() {
        var userId = mockUtil.savedAdmin().getId();
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
        var token1 = MAPPER.toTokenDto(mockUtil.savedExpiredToken(tokenType()));
        var userId = token1.getUserId();
        var count1 = tokenRepository.findAll().size();
        var token2 = tokenService().getOrCreateTokenForUser(userId);
        var count2 = tokenRepository.findAll().size();
        assertEquals(count1 + 1, count2);
        assertNotEquals(token1, token2);
        assertEquals(token1.getUserId(), token2.getUserId());
    }

    @Test(expected = TokenException.class)
    public void getTokenByUserId_tokenNotFound() {
        var userId = mockUtil.savedAdmin().getId();
        tokenService().getTokenByUserId(userId);
    }

    @Test
    public void getTokenByUserId_expiredToken() {
        var userId = mockUtil.savedExpiredToken(tokenType()).getUserId();
        var token = tokenService().getTokenByUserId(userId);
        assertNotNull(token);
    }

    @Test
    public void getTokenByUserId() {
        var userId = mockUtil.savedToken(tokenType()).getUserId();
        var token = tokenService().getTokenByUserId(userId);
        assertNotNull(token);
    }

    @Test
    public void deleteExpiredTokens_noTokens() {
        assertEquals(List.of(), tokenRepository.findAll());
        tokenService().deleteExpiredTokens();
        assertEquals(List.of(), tokenRepository.findAll());
    }

    @Test
    public void deleteExpiredTokens_noExpiredTokens() {
        var token = mockUtil.savedToken(tokenType());
        assertEquals(Set.of(token), Set.copyOf(tokenRepository.findAll()));
        tokenService().deleteExpiredTokens();
        assertEquals(Set.of(token), Set.copyOf(tokenRepository.findAll()));
    }

    @Test
    public void deleteExpiredTokens() {
        var token = mockUtil.savedToken(tokenType());
        var expiredToken = mockUtil.savedExpiredToken(tokenType());
        assertEquals(
                Set.copyOf(tokenRepository.findAll()),
                Set.of(token, expiredToken));
        tokenService().deleteExpiredTokens();
        assertEquals(
                Set.copyOf(tokenRepository.findAll()),
                Set.of(token));
    }

}
