package com.bloxico.ase.userservice.service.token.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import com.bloxico.ase.userservice.util.mapper.JwtToDtoMapper;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.time}")
    private long expirationTime;

    private final BlacklistedJwtRepository blacklistedJwtRepository;

    @Autowired
    public JwtServiceImpl(BlacklistedJwtRepository blacklistedJwtRepository) {
        this.blacklistedJwtRepository = blacklistedJwtRepository;
    }

    private Date expiresAt() {
        return Date
                .from(LocalDateTime
                        .now()
                        .plusSeconds(expirationTime)
                        .atZone(systemDefault())
                        .toInstant());
    }

    @Override
    public String generateToken(UserProfileDto userProfile) {
        log.debug("JwtServiceImpl.generateToken - start | userProfile: {}", userProfile);
        requireNonNull(userProfile);
        var token = JWT
                .create()
                .withIssuedAt(new Date())
                .withExpiresAt(expiresAt())
                .withClaim("id", userProfile.getId())
                .withClaim("roles", userProfile.streamRoleNames().collect(toList()))
                .withClaim("permissions", userProfile.streamPermissionNames().collect(toList()))
                .sign(Algorithm.HMAC256(secret));
        log.debug("JwtServiceImpl.generateToken - end | userProfile: {}", userProfile);
        return token;
    }

    @Override
    public DecodedJwtDto verifyToken(String token) {
        log.debug("JwtServiceImpl.verifyToken - start | token: {}", token);
        requireNonNull(token);
        if (JwtBlacklistInMemory.contains(token))
            throw ErrorCodes.Jwt.INVALID_TOKEN.newException();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException ex) {
            throw ErrorCodes.Jwt.INVALID_TOKEN.newException(ex);
        }
        log.debug("JwtServiceImpl.verifyToken - end | token: {}", token);
        return JwtToDtoMapper.INSTANCE.decodedJwt(decodedJWT);
    }

    @Override
    public void blacklistToken(long principalId, String token) {
        log.debug("JwtServiceImpl.blacklistToken - start | token: {}", token);
        requireNonNull(token);
        // Race condition below is tolerated, duplicate tokens may exist in db
        if (!JwtBlacklistInMemory.contains(token)) {
            var blacklistedJwt = new BlacklistedJwt();
            blacklistedJwt.setToken(token);
            blacklistedJwt.setCreatorId(principalId);
            blacklistedJwtRepository.save(blacklistedJwt);
            JwtBlacklistInMemory.add(token);
        }
        log.debug("JwtServiceImpl.blacklistToken - end | token: {}", token);
    }

}
