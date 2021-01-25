package com.bloxico.ase.userservice.dto.entity.user.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OwnerDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

}
