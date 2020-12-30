package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PendingEvaluatorDto {

    @JsonProperty("token")
    String token;

    @JsonProperty("email")
    String email;

    @JsonProperty("status")
    Status status;

    @JsonProperty("cv_path")
    String cvPath;

}
