package com.bloxico.ase.userservice.service.token.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.time.ZoneId.systemDefault;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${token.expiration.time}")
    private long expirationTime;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    public JwtServiceImpl(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
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
                .sign(Algorithm.HMAC256(secret));
        log.debug("JwtServiceImpl.generateToken - end | userProfile: {}", userProfile);
        return token;
    }

    @Override
    public DecodedJwtDto verifyToken(String token) {
        log.debug("JwtServiceImpl.verifyToken - start | token: {}", token);
        requireNonNull(token);
        if (blacklistedTokens().contains(token))
            throw ErrorCodes.Token.INVALID_TOKEN.newException();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException ex) {
            throw ErrorCodes.Token.INVALID_TOKEN.newException(ex);
        }
        var decodedJwtDto = MAPPER.toDecodedJwtDto(decodedJWT);
        log.debug("JwtServiceImpl.verifyToken - end | token: {}", token);
        return decodedJwtDto;
    }

    @Override
    @Cacheable("blacklistedTokensCache")
    public Set<String> blacklistedTokens() {
        log.debug("JwtServiceImpl.blacklistedTokens - start");
        var blacklist = blacklistedTokenRepository.findDistinctTokenValues();
        log.debug("JwtServiceImpl.blacklistedTokens - end");
        return Set.copyOf(blacklist); // immutable set
    }

    @Override
    @CacheEvict(value = "blacklistedTokensCache", allEntries = true)
    public void blacklistTokens(List<OAuthAccessTokenDto> tokens, long principalId) {
        log.debug("JwtServiceImpl.blacklistTokens - start | tokens: {}, principalId: {}", tokens, principalId);
        requireNonNull(tokens);
        var bTokens = tokens
                .stream()
                .map(MAPPER::toBlacklistedToken)
                .peek(bt -> bt.setCreatorId(principalId))
                .collect(toList());
        blacklistedTokenRepository.saveAll(bTokens);
        blacklistedTokenRepository.flush();
        log.debug("JwtServiceImpl.blacklistTokens - end | tokens: {}, principalId: {}", tokens, principalId);
    }

    @Override
    public void checkIfBlacklisted(String token) {
        if (blacklistedTokens().contains(token))
            throw ErrorCodes.Token.INVALID_TOKEN.newException();
    }

}
