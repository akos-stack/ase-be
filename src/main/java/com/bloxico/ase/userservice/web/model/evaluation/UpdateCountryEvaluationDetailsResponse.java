package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateCountryEvaluationDetailsResponse {

    @JsonProperty("country_evaluation_details")
    CountryEvaluationDetailsDto countryEvaluationDetails;

}
