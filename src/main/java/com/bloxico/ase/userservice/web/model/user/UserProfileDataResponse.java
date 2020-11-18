package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserProfileDataResponse {

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

}
