package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveCountryEvaluationDetailsResponse {

    CountryEvaluationDetailsDto countryEvaluationDetails;

}
