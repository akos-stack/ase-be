package com.bloxico.ase.userservice.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CountryTotalOfEvaluatorsProj {

    @JsonProperty
    int id;

    @JsonProperty
    String name;

    @JsonProperty
    String region;

    @JsonProperty("price_per_evaluation")
    int pricePerEvaluation;

    @JsonProperty("availability_percentage")
    int availabilityPercentage;

    @JsonProperty("total_of_evaluators")
    long totalOfEvaluators;

}
