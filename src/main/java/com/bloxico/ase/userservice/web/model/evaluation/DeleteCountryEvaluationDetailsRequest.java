package com.bloxico.ase.userservice.web.model.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DeleteCountryEvaluationDetailsRequest {

    @NotNull
    @JsonProperty("id")
    @ApiModelProperty(required = true)
    Long id;

}
