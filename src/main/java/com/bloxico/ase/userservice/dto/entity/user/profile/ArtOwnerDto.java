package com.bloxico.ase.userservice.dto.entity.user.profile;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArtOwnerDto extends BaseEntityDto {

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

}
