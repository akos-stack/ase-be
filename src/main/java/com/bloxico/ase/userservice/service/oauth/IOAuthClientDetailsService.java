package com.bloxico.ase.userservice.service.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthClientDetailsDto;

public interface IOAuthClientDetailsService {

    OAuthClientDetailsDto findOAuthClientDetailsByClientId(String clientId);

}
