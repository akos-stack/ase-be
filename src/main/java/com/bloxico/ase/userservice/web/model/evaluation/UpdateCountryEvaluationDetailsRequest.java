package com.bloxico.ase.userservice.web.model.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateCountryEvaluationDetailsRequest {

    @NotNull
    @JsonProperty("id")
    @ApiModelProperty(required = true)
    Integer id;

    @NotNull
    @Min(1)
    @JsonProperty("price_per_evaluation")
    @ApiModelProperty(required = true)
    Integer pricePerEvaluation;

    @NotNull
    @Min(1)
    @Max(100)
    @JsonProperty("availability_percentage")
    @ApiModelProperty(required = true)
    Integer availabilityPercentage;

}
