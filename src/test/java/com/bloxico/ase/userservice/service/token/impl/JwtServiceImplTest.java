package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.exception.JwtException;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JwtServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

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
        jwtService.blacklistToken(principalId, token);
        jwtService.verifyToken(token);
    }

    @Test(expected = NullPointerException.class)
    public void blacklistToken_nullToken() {
        var principalId = mockUtil.savedAdmin().getId();
        jwtService.blacklistToken(principalId, null);
    }

    @Test
    public void blacklist_generated_token() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        jwtService.blacklistToken(principalId, token);
        assertTrue(JwtBlacklistInMemory.contains(token));
        var tokens = blacklistedJwtRepository
                .findAll()
                .stream()
                .map(BlacklistedJwt::getToken)
                .collect(toList());
        assertEquals(singletonList(token), tokens);
    }

    @Test
    public void blacklist_same_token_multiple_times() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        jwtService.blacklistToken(principalId, token);
        assertTrue(JwtBlacklistInMemory.contains(token));
        var tokens = blacklistedJwtRepository
                .findAll()
                .stream()
                .map(BlacklistedJwt::getToken)
                .collect(toList());
        assertEquals(singletonList(token), tokens);
        jwtService.blacklistToken(principalId, token);
        tokens = blacklistedJwtRepository
                .findAll()
                .stream()
                .map(BlacklistedJwt::getToken)
                .collect(toList());
        assertEquals(singletonList(token), tokens);
    }

}
