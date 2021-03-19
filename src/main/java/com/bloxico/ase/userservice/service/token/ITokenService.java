package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.TokenDto;

import java.lang.reflect.Type;
import java.util.List;

public interface ITokenService {

    TokenDto createTokenForUser(long userId);

    TokenDto refreshToken(String token);

    TokenDto consumeToken(String token);

    TokenDto getOrCreateTokenForUser(long userId);

    TokenDto getTokenByUserId(long userId);

    List<Long> deleteExpiredTokens();

    void requireTokenNotExistsForUser(long userId);

}
