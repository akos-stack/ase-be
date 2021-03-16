package com.bloxico.ase.userservice.dto.entity.user.profile;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluatorDto extends BaseEntityDto {

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

    @JsonProperty("verified_at")
    LocalDateTime verifiedAt;

}
