package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = false)
public class QuotationPackageCountryDto extends BaseEntityAuditDto {

    @JsonProperty("quotation_package_id")
    private Long quotationPackageId;

    @JsonProperty("country_id")
    private Long countryId;

    @JsonProperty("number_of_evaluations")
    private Integer numberOfEvaluations;

}
