package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluatorDto {

    Long id;
    UserProfileDto userProfile;
    LocalDateTime verifiedAt;

}
