package com.bloxico.userservice.services.token.impl;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.token.Token;
import com.bloxico.userservice.entities.token.RegistrationTokenFactory;
import com.bloxico.userservice.exceptions.TokenException;
import com.bloxico.userservice.repository.token.TokenRepository;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.util.DateUtil;
import com.bloxico.userservice.util.mappers.EntityDataMapper;
import com.bloxico.userservice.util.tokencreator.TokenCreatorUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TokenServiceImpl<T extends Token> implements ITokenService<T> {

    private TokenRepository<T> tokenRepository;

    private TokenCreatorUtil<T> tokenCreatorUtil;

    private RegistrationTokenFactory registrationTokenFactory;

    private EntityDataMapper entityDataMapper = EntityDataMapper.INSTANCE;

    @Value("${token.expiration.time}")
    private int expirationTime;

    public TokenServiceImpl(TokenRepository<T> tokenRepository, TokenCreatorUtil<T> tokenCreatorUtil, RegistrationTokenFactory registrationTokenFactory) {
        this.tokenRepository = tokenRepository;
        this.tokenCreatorUtil = tokenCreatorUtil;
        this.registrationTokenFactory = registrationTokenFactory;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public TokenDto createTokenForUser(Long userId) {
        log.debug("Create token for user - start");

        T token = (T) registrationTokenFactory.getInstance(this);

        addTokenCodeAndExpiryDate(token);
        token.setUserId(userId);

        tokenRepository.save(token);

        TokenDto tokenDto = entityDataMapper.tokenToDto(token);
        log.debug("Create token for user - end , token: {}", tokenDto);

        return tokenDto;
    }

    @Override
    public TokenDto refreshExpiredToken(String expiredTokenValue) {
        log.debug("Refreshing expired token - start , value: {}", expiredTokenValue);

        T token = getTokenByTokenValue(expiredTokenValue);
        addTokenCodeAndExpiryDate(token);

        tokenRepository.save(token);

        TokenDto tokenDto = entityDataMapper.tokenToDto(token);

        log.debug("Refreshing expired token - end");
        return tokenDto;
    }

    private void addTokenCodeAndExpiryDate(T token) {

        String tokenCode = tokenCreatorUtil.createTokenCode();

        token.setTokenValue(tokenCode);
        token.setExpiryDate(DateUtil.addMinutesToGivenDate(LocalDateTime.now(), expirationTime));
    }

    /**
     * Deletes token for user if it is valid
     *
     * @param userId     User ID to whom this token is related
     * @param tokenValue Value of a token that needs to be consumed
     */
    @Override
    public void consumeTokenForUser(Long userId, String tokenValue) {

        T token = getNonExpiredTokenByTokenValue(tokenValue);

        if (!userId.equals(token.getUserId())) {
            throw new TokenException(ErrorCodes.TOKEN_NOT_FOUND.getCode());
        }

        tokenRepository.delete(token);
    }

    private T getNonExpiredTokenByTokenValue(String tokenValue) {
        log.debug("Getting non expired token by value - start , value: {}", tokenValue);

        T token = getTokenByTokenValue(tokenValue);

        if (tokenExpired(token)) {
            log.warn("Token expired - throwing Exception.");
            throw new TokenException(ErrorCodes.TOKEN_EXPIRED.getCode());
        }

        log.debug("Getting non expired token by value - end , token: {}", token);
        return token;
    }

    @Override
    public TokenDto createNewOrReturnNonExpiredTokenForUser(Long userId) {
        log.debug("Creating new or returning non expired token - start, user id: {}", userId);
        Optional<T> op = tokenRepository.findNonExpiredByUserId(userId);

        TokenDto tokenDto;

        if (op.isPresent()) {
            T token = op.get();
            tokenDto = entityDataMapper.tokenToDto(token);

            log.debug("Token found, returning non expired token: {} ", tokenDto);
        } else {
            log.debug("Could not find non expired token, creating new one");
            tokenDto = createTokenForUser(userId);
        }

        log.debug("Creating new or returning non expired token - end , user id: {}", userId);
        return tokenDto;
    }

    private boolean tokenExpired(T token) {
        return token.getExpiryDate().before(new Date());
    }

    private T getTokenByTokenValue(String tokenValue) {
        log.debug("Get token by token value {} - start , token value: {}", tokenValue);
        Optional<T> op = tokenRepository.findByTokenValue(tokenValue);
        T token = extractFromOptional(op);

        log.debug("Get token by token value {} - end, token: {}", token);

        return token;
    }

    @Override
    public TokenDto getTokenByUserId(Long id) {
        log.debug("Get token by user id - start , id: {}", id);
        Optional<T> op = tokenRepository.findByUserId(id);
        T token = extractFromOptional(op);

        TokenDto tokenDto = entityDataMapper.tokenToDto(token);

        log.debug("Get token by user id - end , token found, returning token {}", token);
        return tokenDto;
    }

    private T extractFromOptional(Optional<T> op) {
        return op.orElseThrow(() -> new TokenException(ErrorCodes.TOKEN_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional
    public List<Long> deleteExpiredTokens() {
        log.debug("Delete expired tokens - start");
        List<T> expiredTokens = tokenRepository.findExpiredTokens();

        log.debug("Found {} expired tokens.", expiredTokens.size());

        List<Long> userIdsAssignedToExpiredTokens = expiredTokens.stream()
                .map(Token::getUserId)
                .collect(Collectors.toList());

        List<Long> tokenIdsToDelete = expiredTokens.stream()
                .map(Token::getId)
                .collect(Collectors.toList());

        log.debug("Deleting by id {}", tokenIdsToDelete.toString());
        tokenRepository.deleteByIdIn(tokenIdsToDelete);

        log.debug("Delete expired tokens - end");
        return userIdsAssignedToExpiredTokens;
    }
}
