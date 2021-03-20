package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_EXISTS;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
abstract class AbstractTokenServiceImpl implements ITokenService {

    @Value("${token.expiry.time}")
    private int expiryTime;

    private final TokenRepository tokenRepository;

    protected AbstractTokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    protected abstract Token.Type getType();

    @Override
    public TokenDto createTokenForUser(long userId) {
        var type = getType();
        log.debug("TokenServiceImpl[{}].createTokenForUser - start | userId: {}", type, userId);
        var token = new Token();
        token.setValue(newTokenValue());
        token.setType(type);
        token.setUserId(userId);
        token.setCreatorId(userId);
        token.setExpiryDate(newExpiryDate());
        token = tokenRepository.saveAndFlush(token);
        var tokenDto = MAPPER.toDto(token);
        log.debug("TokenServiceImpl[{}].createTokenForUser - end | userId: {}", type, userId);
        return tokenDto;
    }

    @Override
    public TokenDto refreshToken(String tokenValue) {
        log.debug("TokenServiceImpl.refreshToken - start | tokenValue: {}", tokenValue);
        requireNonNull(tokenValue);
        var token = tokenRepository
                .findByValue(tokenValue)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        token.setValue(newTokenValue());
        token.setExpiryDate(newExpiryDate());
        token.setUpdaterId(token.getCreatorId());
        token = tokenRepository.saveAndFlush(token);
        var tokenDto = MAPPER.toDto(token);
        log.debug("TokenServiceImpl.refreshToken - end | tokenValue: {}", tokenValue);
        return tokenDto;
    }

    @Override
    public TokenDto consumeToken(String tokenValue) {
        log.debug("TokenServiceImpl.consumeToken - start | tokenValue: {}", tokenValue);
        requireNonNull(tokenValue);
        var tokenDto = tokenRepository
                .findByValue(tokenValue)
                .filter(not(Token::isExpired))
                .map(doto(tokenRepository::delete))
                .map(MAPPER::toDto)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        log.debug("TokenServiceImpl.consumeToken - start | tokenValue: {}", tokenValue);
        return tokenDto;
    }

    @Override
    public TokenDto getOrCreateTokenForUser(long userId) {
        var type = getType();
        log.debug("TokenServiceImpl[{}].getOrCreateTokenForUser - start | userId: {}", type, userId);
        var tokenDto = tokenRepository
                .findByTypeAndUserId(type, userId)
                .filter(not(Token::isExpired))
                .map(MAPPER::toDto)
                .orElseGet(() -> createTokenForUser(userId));
        log.debug("TokenServiceImpl[{}].getOrCreateTokenForUser - end | userId: {}", type, userId);
        return tokenDto;
    }

    @Override
    public TokenDto getTokenByUserId(long userId) {
        var type = getType();
        log.debug("TokenServiceImpl[{}].getTokenByUserId - start | userId: {}", type, userId);
        var tokenDto = tokenRepository
                .findByTypeAndUserId(type, userId)
                .map(MAPPER::toDto)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        log.debug("TokenServiceImpl[{}].getTokenByUserId - end | userId: {}", type, userId);
        return tokenDto;
    }

    @Override
    public List<Long> deleteExpiredTokens() {
        var type = getType();
        log.debug("TokenServiceImpl[{}].deleteExpiredTokens - start", type);
        var expired = tokenRepository.findAllExpiredTokensByType(type);
        tokenRepository.deleteInBatch(expired);
        var userIds = expired
                .stream()
                .map(Token::getUserId)
                .collect(toUnmodifiableList());
        log.debug("TokenServiceImpl[{}].deleteExpiredTokens - end", type);
        return userIds;
    }

    @Override
    public void requireTokenNotExistsForUser(long userId) {
        var type = getType();
        log.debug("TokenServiceImpl[{}].requireTokenNotExistsForUser - start | userId: {}", type, userId);
        if (tokenRepository.findByTypeAndUserId(type, userId).isPresent())
            throw TOKEN_EXISTS.newException();
        log.debug("TokenServiceImpl[{}].requireTokenNotExistsForUser - end | userId: {}", type, userId);
    }

    @Override
    public TokenDto checkIfTokenExists(String tokenValue) {
        log.debug("TokenServiceImpl.checkIfTokenExists - start | tokenValue: {}", tokenValue);
        requireNonNull(tokenValue);
        var tokenDto = tokenRepository
                .findByValue(tokenValue)
                .filter(not(Token::isExpired))
                .map(MAPPER::toDto)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        log.debug("TokenServiceImpl.checkIfTokenExists - end | tokenValue: {}", tokenValue);
        return tokenDto;
    }

    @Override
    public void equalityWithPrincipalId(long principalId, Long tokenUserId){
        log.debug("TokenServiceImpl.equalityWithPrincipalId - start | principalId: {}, tokenUserId: {}", principalId, tokenUserId);
        requireNonNull(principalId);
        requireNonNull(tokenUserId);
        if (principalId != tokenUserId)
            throw ErrorCodes.Token.EQUALITY_WITH_PRINCIPAL_ID_ERROR.newException();
        log.debug("TokenServiceImpl.equalityWithPrincipalId - end | principalId: {}, tokenUserId: {}", principalId, tokenUserId);
    }

    private static String newTokenValue() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime newExpiryDate() {
        return LocalDateTime.now().plusMinutes(expiryTime);
    }

}
