package com.bloxico.ase.userservice.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RegionDetailsProj {

    @JsonProperty
    int id;

    @JsonProperty
    String name;

    @JsonProperty("number_of_countries")
    long numberOfCountries;

    @JsonProperty("number_of_evaluators")
    long numberOfEvaluators;

}
