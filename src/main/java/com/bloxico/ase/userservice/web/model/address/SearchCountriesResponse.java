package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchCountriesResponse {

    @JsonProperty("countries")
    List<CountryTotalOfEvaluatorsProj> countries;

    @JsonProperty("total_elements")
    long totalElements;

}
