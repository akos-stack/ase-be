package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.exception.JwtException;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JwtServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Test(expected = NullPointerException.class)
    public void generateToken_nullUserProfile() {
        jwtService.generateToken(null);
    }

    @Test(expected = NullPointerException.class)
    public void verifyToken_nullToken() {
        jwtService.verifyToken(null);
    }

    @Test(expected = JwtException.class)
    public void verifyToken_invalidToken() {
        jwtService.verifyToken("foo");
    }

    @Test
    public void verify_generated_token() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        var decodedJwtDto = jwtService.verifyToken(token);
        assertEquals(
                userProfileDto.getId(),
                decodedJwtDto.getUserId());
        assertEquals(
                userProfileDto
                        .streamRoleNames()
                        .collect(toSet()),
                decodedJwtDto.getRoles());
    }

    @Test(expected = JwtException.class)
    public void verifyToken_blacklistedToken() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        jwtService.blacklistTokens(oTokens, principalId);
        assertTrue(jwtService.blacklistedTokens().contains(token));
        jwtService.verifyToken(token);
    }

    @Test(expected = NullPointerException.class)
    public void blacklistTokens_nullTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        jwtService.blacklistTokens(null, principalId);
    }

    @Test
    public void blacklistTokens_emptyTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        jwtService.blacklistTokens(List.of(), principalId);
        assertTrue(jwtService.blacklistedTokens().isEmpty());
    }

    @Test
    public void blacklistTokens_generatedTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        jwtService.blacklistTokens(oTokens, principalId);
        assertTrue(jwtService.blacklistedTokens().contains(token));
        var tokens = blacklistedTokenRepository.findDistinctTokenValues();
        assertEquals(List.of(token), tokens);
    }

    @Test
    public void blacklistTokens_sameTokenMultipleTimes() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        jwtService.blacklistTokens(oTokens, principalId);
        assertTrue(jwtService.blacklistedTokens().contains(token));
        assertEquals(
                List.of(token),
                blacklistedTokenRepository.findDistinctTokenValues());
        jwtService.blacklistTokens(oTokens, principalId);
        assertEquals(
                List.of(token, token),
                blacklistedTokenRepository
                        .findAll()
                        .stream()
                        .map(BlacklistedToken::getToken)
                        .collect(toList()));
    }

    @Test // test with Thread.sleep(...) in blacklistedTokens()
    public void blacklistedTokens_caching() {
        assertTrue(jwtService.blacklistedTokens().isEmpty());
        assertTrue(jwtService.blacklistedTokens().isEmpty());
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        var oTokens = mockUtil.toOAuthAccessTokenDtoList(userProfileDto, token);
        jwtService.blacklistTokens(oTokens, principalId); // evicts cache
        assertFalse(jwtService.blacklistedTokens().isEmpty());
        assertFalse(jwtService.blacklistedTokens().isEmpty());
    }

}
