package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import lombok.Value;

@Value
public class PendingEvaluatorDto {

    String token;
    String email;
    PendingEvaluator.PendingEvaluatorStatus status;
    String cvPath;


}
