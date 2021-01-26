package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import org.springframework.web.multipart.MultipartFile;

public interface IPendingEvaluatorRequest {

    Status getStatus();

    String getEmail();

    MultipartFile getCv();

}
