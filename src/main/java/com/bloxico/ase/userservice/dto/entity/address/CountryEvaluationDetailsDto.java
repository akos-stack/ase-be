package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryEvaluationDetailsDto {

    @JsonProperty
    private Integer id;

    @JsonProperty("price_per_evaluation")
    private int pricePerEvaluation;

    @JsonProperty("availability_percentage")
    private int availabilityPercentage;

    @JsonProperty("total_of_evaluators")
    private int totalOfEvaluators;

}
