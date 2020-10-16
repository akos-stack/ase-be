package com.bloxico.userservice.services.oauth;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import org.springframework.security.oauth2.provider.ClientDetails;

public interface IOauthTokenService {

    String authenticateIntegratedUser(String email);

    String authenticateClientCredentials(String clientId, String secret);

    void deleteExpiredAccessTokens();
}
