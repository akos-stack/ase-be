package com.bloxico.ase.userservice.projection;

import lombok.Value;

@Value
public class CountryTotalOfEvaluatorsProj {

    int id;
    String name;
    String region;
    int pricePerEvaluation;
    int availabilityPercentage;
    long totalOfEvaluators;

}
