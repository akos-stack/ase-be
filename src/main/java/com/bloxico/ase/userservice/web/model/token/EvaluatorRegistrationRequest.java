package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.bloxico.ase.userservice.validator.ValidEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.REQUESTED;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class EvaluatorRegistrationRequest implements IPendingEvaluatorRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("cvPath")
    @ApiModelProperty(required = true)
    String cvPath;

    @JsonIgnore
    @Override
    public Status getStatus() {
        return REQUESTED;
    }

}
