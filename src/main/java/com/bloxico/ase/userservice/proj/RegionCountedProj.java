package com.bloxico.ase.userservice.proj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RegionCountedProj {

    @JsonProperty("id")
    int id;

    @JsonProperty("name")
    String name;

    @JsonProperty("number_of_countries")
    long numberOfCountries;

    @JsonProperty("number_of_evaluators")
    long numberOfEvaluators;

}
