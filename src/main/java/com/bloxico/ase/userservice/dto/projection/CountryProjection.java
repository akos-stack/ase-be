package com.bloxico.ase.userservice.dto.projection;

import lombok.Value;

@Value
public class CountryProjection {

    Integer countryId;
    String countryName;

    Integer regionId;
    String regionName;

    Integer countryEvaluationDetailsId;
    Integer pricePerEvaluation;
    Integer availabilityPercentage;
    Long totalOfEvaluators;

}
