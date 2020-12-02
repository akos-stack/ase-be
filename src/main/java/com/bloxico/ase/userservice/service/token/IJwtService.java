package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;

import java.util.List;
import java.util.Set;

public interface IJwtService {

    String generateToken(UserProfileDto userProfile);

    DecodedJwtDto verifyToken(String token);

    Set<String> blacklistedTokens();

    void blacklistTokens(List<OAuthAccessTokenDto> tokens, long principalId);

}
