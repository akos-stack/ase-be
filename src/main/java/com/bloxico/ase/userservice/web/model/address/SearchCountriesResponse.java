package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class SearchCountriesResponse {

    @JsonProperty("countries")
    List<CountryTotalOfEvaluatorsProj> countries;

}
