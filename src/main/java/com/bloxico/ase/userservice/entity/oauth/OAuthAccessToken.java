package com.bloxico.ase.userservice.entity.oauth;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "oauth_access_token")
@Entity
public class OAuthAccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token")
    private byte[] token;

    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "authentication")
    private byte[] authentication;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @Column(name = "refresh_token")
    private String refreshToken;

}
