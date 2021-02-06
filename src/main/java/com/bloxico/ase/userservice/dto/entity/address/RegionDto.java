package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@EqualsAndHashCode(of = "name")
public class RegionDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("number_of_countries")
    @JsonInclude(NON_NULL)
    private Integer numberOfCountries;

    @JsonProperty("number_of_evaluators")
    @JsonInclude(NON_NULL)
    private Integer numberOfEvaluators;

}
