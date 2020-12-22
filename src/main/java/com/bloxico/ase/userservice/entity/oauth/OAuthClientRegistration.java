package com.bloxico.ase.userservice.entity.oauth;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@ToString(exclude = "clientSecret")
@Entity
@Table(name = "oauth_client_registrations")
public class OAuthClientRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_authentication_method")
    private String clientAuthenticationMethod;

    @Column(name = "authorization_grant_type")
    private String authorizationGrantType;

    @Column(name = "redirect_uri_template")
    private String redirectUriTemplate;

    @Column(name = "scopes")
    private String scopes;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "authorization_uri")
    private String authorizationUri;

    @Column(name = "token_uri")
    private String tokenUri;

    @Column(name = "user_info_uri")
    private String userInfoUri;

    @Column(name = "user_info_authentication_method")
    private String userInfoAuthenticationMethod;

    @Column(name = "user_name_attribute_name")
    private String userNameAttributeName;

    @Column(name = "jwk_set_uri")
    private String jwkSetUri;

}
