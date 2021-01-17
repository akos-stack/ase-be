package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.bloxico.ase.userservice.validator.ValidEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@Setter
public class EvaluatorRegistrationRequest implements IPendingEvaluatorRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("cv")
    @ApiModelProperty(required = true)
    MultipartFile cv;

    @Override
    public Status getStatus() {
        return REQUESTED;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public MultipartFile getCv() {
        return this.cv;
    }

}
