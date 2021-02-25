package com.bloxico.ase.userservice.proj;

import com.bloxico.ase.userservice.entity.address.Country;
import lombok.Value;

@Value
public class CountryEvaluationDetailsCountedTransferProj {
    Country country;
    Integer countryId;
    String countryName;
    Integer evaluationDetailsId;
    Integer pricePerEvaluation;
    Integer availabilityPercentage;
    Long totalOfEvaluators;
}
