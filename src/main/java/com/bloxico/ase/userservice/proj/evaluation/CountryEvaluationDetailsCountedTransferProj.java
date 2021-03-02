package com.bloxico.ase.userservice.proj.evaluation;

import com.bloxico.ase.userservice.entity.address.Country;
import lombok.Value;

@Value
public class CountryEvaluationDetailsCountedTransferProj {

    Country country;
    Long countryId;
    String countryName;
    Long evaluationDetailsId;
    Integer pricePerEvaluation;
    Integer availabilityPercentage;
    Long totalOfEvaluators;

}
