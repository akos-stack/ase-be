package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class UserProfileDataResponse {

    @JsonProperty("user_profile")
    @ApiModelProperty(required = true)
    UserProfileDto userProfile;

}
