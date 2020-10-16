package com.bloxico.userservice.services.token;

import com.bloxico.userservice.dto.entities.TokenDto;

import java.util.List;

public interface ITokenService<T> {

    TokenDto createTokenForUser(Long userId);

    TokenDto refreshExpiredToken(String expiredTokenValue);

    void consumeTokenForUser(Long userId, String tokenValue);

    TokenDto createNewOrReturnNonExpiredTokenForUser(Long userId);

    TokenDto getTokenByUserId(Long id);

    List<Long> deleteExpiredTokens();
}
