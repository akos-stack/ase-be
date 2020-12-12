package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.TokenDto;

public interface ITokenService {

    TokenDto createTokenForUser(long userId);

    TokenDto refreshToken(String token);

    void consumeTokenForUser(String token, long userId);

    TokenDto getOrCreateTokenForUser(long userId);

    TokenDto getTokenByUserId(long userId);

    void deleteExpiredTokens();

}
