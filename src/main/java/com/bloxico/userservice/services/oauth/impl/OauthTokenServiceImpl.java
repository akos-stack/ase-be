package com.bloxico.userservice.services.oauth.impl;

import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.userservice.config.oauth2.CoinClientDetailsService;
import com.bloxico.userservice.config.oauth2.CustomJdbcTokenStore;
import com.bloxico.userservice.repository.oauth.AccessTokenRepository;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.services.oauth.IOauthTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OauthTokenServiceImpl implements IOauthTokenService {

    private AccessTokenRepository accessTokenRepository;

    @Autowired
    public OauthTokenServiceImpl(AccessTokenRepository accessTokenRepository,
                                 CoinUserRepository coinUserRepository,
                                 CoinClientDetailsService coinClientDetailsService,
                                 CustomJdbcTokenStore customJdbcTokenStore,
                                 UserProfileRepository userProfileRepository) {
        this.accessTokenRepository = accessTokenRepository;

    }

    @Override
    public void deleteExpiredAccessTokens() {
        log.debug("Deleting expired oauth access tokens - start");

        accessTokenRepository.deleteExpiredTokens();

        log.debug("Deleting expired oauth access tokens - end");
    }
}
