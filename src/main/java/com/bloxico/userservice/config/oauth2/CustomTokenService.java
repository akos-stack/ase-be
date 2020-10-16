package com.bloxico.userservice.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomTokenService extends DefaultTokenServices {

    @Autowired
    public CustomTokenService(CustomJdbcTokenStore tokenStore, CoinClientDetailsService coinClientDetailsService) {
        setTokenStore(tokenStore);
        setClientDetailsService(coinClientDetailsService);
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {

        try {
            return super.createAccessToken(authentication);
        } catch (DuplicateKeyException ex) {
            log.info("There was a duplicate key exception - instead of creating, returning existing token");
            return super.getAccessToken(authentication);
        }
    }
}