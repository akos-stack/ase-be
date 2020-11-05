package com.bloxico.ase.userservice.web.model.auth;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AuthenticationResponse {

    @JsonProperty("token")
    String token;

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

}
