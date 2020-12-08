package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;

import java.util.List;
import java.util.Set;

public interface ITokenBlacklistService {

    Set<String> blacklistedTokens();

    void blacklistTokens(List<OAuthAccessTokenDto> tokens, long principalId);

    void checkIfBlacklisted(String token);

}
