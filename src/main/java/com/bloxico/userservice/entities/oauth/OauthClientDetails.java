package com.bloxico.userservice.entities.oauth;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_client_details")
@Data
public class OauthClientDetails {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "resource_ids")
    private String resource_ids;

    @Column(name = "client_secret")
    private String client_secret;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorized_grant_types")
    private String authorized_grant_types;

    @Column(name = "web_server_redirect_uri")
    private String web_server_redirect_uri;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "access_token_validity")
    private Integer access_token_validity;

    @Column(name = "refresh_token_validity")
    private Integer refresh_token_validity;

    @Column(name = "additional_information")
    private String additional_information;

    @Column(name = "autoapprove")
    private String autoapprove;

}
