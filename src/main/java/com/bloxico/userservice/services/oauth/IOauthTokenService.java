package com.bloxico.userservice.services.oauth;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import org.springframework.security.oauth2.provider.ClientDetails;

public interface IOauthTokenService {

    void deleteExpiredAccessTokens();
}
