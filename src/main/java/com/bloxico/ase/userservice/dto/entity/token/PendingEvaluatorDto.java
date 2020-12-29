package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import lombok.Value;

@Value
public class PendingEvaluatorDto {

    String email;
    String token;
    Status status;
    String cvPath;

}
