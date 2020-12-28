package com.bloxico.ase.userservice.dto.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluatorDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("user_profile")
    UserProfileDto userProfile;

    @JsonProperty("verified_at")
    LocalDateTime verifiedAt;

}
