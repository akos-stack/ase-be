package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthClientDetailsDto;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import com.bloxico.ase.userservice.service.oauth.IOAuthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class OAuthClientDetailsServiceImpl implements IOAuthClientDetailsService, ClientDetailsService {

    private final OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Autowired
    public OAuthClientDetailsServiceImpl(OAuthClientDetailsRepository oAuthClientDetailsRepository) {
        this.oAuthClientDetailsRepository = oAuthClientDetailsRepository;
    }

    @Override
    public OAuthClientDetailsDto findOAuthClientDetailsByClientId(String clientId) {
        log.debug("OAuthClientDetailsServiceImpl.findOAuthClientDetailsByClientId - start | clientId: {}", clientId);
        requireNonNull(clientId);
        var dto = oAuthClientDetailsRepository
                .findByClientId(clientId)
                .orElseThrow(EntityNotFoundException::new);
        log.debug("OAuthClientDetailsServiceImpl.findOAuthClientDetailsByClientId - end | clientId: {}", clientId);
        return dto;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        log.debug("OAuthClientDetailsServiceImpl.loadClientByClientId - start | clientId: {}", clientId);
        requireNonNull(clientId);
        var dto = findOAuthClientDetailsByClientId(clientId);
        var bcd = new BaseClientDetails();
        bcd.setClientId(dto.getClientId());
        bcd.setClientSecret(dto.getClientSecret());
        bcd.setScope(dto.scopesAsSet());
        bcd.setAuthorizedGrantTypes(dto.authorizedGrantTypesAsSet());
        bcd.setAuthorities(dto.authoritiesAsGrantedAuthoritiesSet());
        bcd.setAccessTokenValiditySeconds(dto.getAccessTokenValidity());
        log.debug("OAuthClientDetailsServiceImpl.loadClientByClientId - end | clientId: {}", clientId);
        return bcd;
    }

}
