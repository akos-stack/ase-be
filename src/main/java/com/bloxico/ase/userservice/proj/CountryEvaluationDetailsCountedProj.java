package com.bloxico.ase.userservice.proj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CountryEvaluationDetailsCountedProj {

    @JsonProperty("country")
    String country;

    @JsonProperty("region")
    String region;

    @JsonProperty("price_per_evaluation")
    Integer pricePerEvaluation;

    @JsonProperty("availability_percentage")
    Integer availabilityPercentage;

    @JsonProperty("total_of_evaluators")
    Long totalOfEvaluators;

}
