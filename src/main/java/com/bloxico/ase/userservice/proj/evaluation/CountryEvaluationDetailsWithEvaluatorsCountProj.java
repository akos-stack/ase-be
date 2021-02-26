package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class CountryEvaluationDetailsWithEvaluatorsCountProj {

    @JsonProperty("country_id")
    Integer countryId;

    @JsonProperty("country")
    String country;

    @JsonProperty("regions")
    List<String> regions;

    @JsonProperty("evaluation_details_id")
    Integer evaluationDetailsId;

    @JsonProperty("price_per_evaluation")
    Integer pricePerEvaluation;

    @JsonProperty("availability_percentage")
    Integer availabilityPercentage;

    @JsonProperty("total_of_evaluators")
    Long totalOfEvaluators;

}
