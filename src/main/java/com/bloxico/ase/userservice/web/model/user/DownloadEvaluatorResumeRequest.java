package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.validator.ValidEmail;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DownloadEvaluatorResumeRequest {

    @NotBlank
    @ValidEmail
    @ApiParam(name = "email", required = true)
    String email;

}
