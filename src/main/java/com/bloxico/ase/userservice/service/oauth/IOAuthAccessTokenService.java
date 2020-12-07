package com.bloxico.ase.userservice.service.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;

import java.util.List;

public interface IOAuthAccessTokenService {

    List<OAuthAccessTokenDto> deleteTokensByEmail(String email);

    void deleteExpiredAccessTokens();
}
