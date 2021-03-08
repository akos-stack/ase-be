package com.bloxico.ase.userservice.dto.entity.evaluation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@EqualsAndHashCode(of = {"quotationPackageId", "countryId"})
public class QuotationPackageCountryDto {

    @JsonProperty("quotation_package_id")
    private Long quotationPackageId;

    @JsonProperty("country_id")
    private Long countryId;

    @JsonProperty("number_of_evaluations")
    private Integer numberOfEvaluations;

}
