package com.bloxico.ase.userservice.dto.entity.oauth;

import lombok.*;
import org.springframework.security.oauth2.core.*;

import java.util.Set;

@Value
@EqualsAndHashCode(of = "registrationId")
@ToString(exclude = "clientSecret")
public class OAuthClientRegistrationDto {

    String registrationId;
    String clientId;
    String clientSecret;
    String clientAuthenticationMethod;
    String authorizationGrantType;
    String redirectUriTemplate;
    String scopes;
    String clientName;
    String authorizationUri;
    String tokenUri;
    String userInfoUri;
    String userInfoAuthenticationMethod;
    String userNameAttributeName;
    String jwkSetUri;

    public Set<String> scopesAsSet() {
        return scopes == null
                ? Set.of()
                : Set.of(scopes.split(","));
    }

    public ClientAuthenticationMethod typedClientAuthenticationMethod() {
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
    }

    public AuthorizationGrantType typedAuthorizationGrantType() {
        return new AuthorizationGrantType(authorizationGrantType);
    }

    public AuthenticationMethod typedUserInfoAuthenticationMethod() {
        return new AuthenticationMethod(userInfoAuthenticationMethod);
    }

}
