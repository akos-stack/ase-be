package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class OAuthAccessTokenServiceImpl implements IOAuthAccessTokenService {

    @Autowired
    private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    @Override
    public List<OAuthAccessTokenDto> deleteTokensByEmail(String email) {
        log.debug("OAuthAccessTokenServiceImpl.deleteTokensByEmail - start | email: {}", email);
        requireNonNull(email);
        var tokens = oAuthAccessTokenRepository.findAllByUserNameIgnoreCase(email);
        oAuthAccessTokenRepository.deleteByUserNameIgnoreCase(email);
        log.debug("OAuthAccessTokenServiceImpl.deleteTokensByEmail - end | email: {}", email);
        return tokens;
    }

    @Override
    public void deleteExpiredAccessTokens() {
        log.debug("Deleting expired oauth access tokens - start");

        oAuthAccessTokenRepository.deleteExpiredTokens();

        log.debug("Deleting expired oauth access tokens - end");
    }

}
