package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveQuotationPackageResponse {

    @JsonProperty("quotation_package")
    QuotationPackageDto quotationPackage;

}
