package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;

public interface IPendingEvaluatorRequest {

    Status getStatus();

    String getEmail();

    String getCvPath();

}
