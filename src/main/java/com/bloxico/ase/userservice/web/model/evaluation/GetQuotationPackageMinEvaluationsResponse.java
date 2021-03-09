package com.bloxico.ase.userservice.web.model.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class GetQuotationPackageMinEvaluationsResponse {

    @JsonProperty("min_evaluations")
    Integer minEvaluations;

}
