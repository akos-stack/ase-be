package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "countryId")
public class CountryEvaluationDetailsDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("country_id")
    private Integer countryId;

    @JsonProperty("price_per_evaluation")
    private Integer pricePerEvaluation;

    @JsonProperty("availability_percentage")
    private Integer availabilityPercentage;

}
