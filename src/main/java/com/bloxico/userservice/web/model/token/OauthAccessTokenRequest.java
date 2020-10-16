package com.bloxico.userservice.web.model.token;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OauthAccessTokenRequest {

    @SerializedName("grant_type")
    private String grantType;
    private String username;
    private String password;
    private String scope;
}
