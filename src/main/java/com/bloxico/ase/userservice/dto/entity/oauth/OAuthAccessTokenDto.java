package com.bloxico.ase.userservice.dto.entity.oauth;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@EqualsAndHashCode(of = {"tokenId", "refreshToken"})
public class OAuthAccessTokenDto {

    String tokenId;
    byte[] token;
    String authenticationId;
    String userName;
    String clientId;
    byte[] authentication;
    LocalDateTime expiration;
    String refreshToken;

}
