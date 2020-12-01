package com.bloxico.ase.userservice.dto.entity.oauth;

import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
public class OAuthClientDetailsDto {

    String clientId;
    String resourceIds;
    String clientSecret;
    String scope;
    String authorizedGrantTypes;
    String webServerRedirectUri;
    String authorities;
    Integer accessTokenValidity;
    Integer refreshTokenValidity;
    String additionalInformation;
    String autoApprove;

    public Set<String> scopesAsSet() {
        return Set.of(scope.split(","));
    }

    public Set<String> authorizedGrantTypesAsSet() {
        return Set.of(authorizedGrantTypes.split(","));
    }

    public Set<GrantedAuthority> authoritiesAsGrantedAuthoritiesSet() {
        return Arrays
                .stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(toUnmodifiableSet());
    }

}
