package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.exception.JwtException;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
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
                userProfileDto.getRole().getName(),
                decodedJwtDto.getRole());
        assertEquals(
                Set.copyOf(userProfileDto.getPermissionNames()),
                decodedJwtDto.getPermissions());
    }

    @Test(expected = JwtException.class)
    public void verifyToken_blacklistedToken() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        jwtService.blacklistToken(1L, token);
        jwtService.verifyToken(token);
    }

    @Test(expected = NullPointerException.class)
    public void blacklistToken_nullToken() {
        jwtService.blacklistToken(1L, null);
    }

    @Test
    public void blacklist_generated_token() {
        blacklistedJwtRepository.deleteAll();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        jwtService.blacklistToken(1L, token);
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
        blacklistedJwtRepository.deleteAll();
        var userProfileDto = mockUtil.savedUserProfileDto();
        var token = jwtService.generateToken(userProfileDto);
        jwtService.blacklistToken(1L, token);
        assertTrue(JwtBlacklistInMemory.contains(token));
        var tokens = blacklistedJwtRepository
                .findAll()
                .stream()
                .map(BlacklistedJwt::getToken)
                .collect(toList());
        assertEquals(singletonList(token), tokens);
        jwtService.blacklistToken(1L, token);
        tokens = blacklistedJwtRepository
                .findAll()
                .stream()
                .map(BlacklistedJwt::getToken)
                .collect(toList());
        assertEquals(singletonList(token), tokens);
    }

}
