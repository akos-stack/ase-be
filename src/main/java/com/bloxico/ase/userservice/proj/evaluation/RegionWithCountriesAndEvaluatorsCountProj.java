package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RegionWithCountriesAndEvaluatorsCountProj {

    @JsonProperty("id")
    int id;

    @JsonProperty("name")
    String name;

    @JsonProperty("number_of_countries")
    long numberOfCountries;

    @JsonProperty("number_of_evaluators")
    long numberOfEvaluators;

}
