package com.bloxico.ase.userservice.proj;

import com.bloxico.ase.userservice.entity.address.Country;
import lombok.Value;

@Value
public class CountryEvaluationDetailsCountedTransferProj {
    Country c;
    String country;
    Integer pricePerEvaluation;
    Integer availabilityPercentage;
    Long totalOfEvaluators;
}
