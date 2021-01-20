package com.bloxico.ase.userservice.web.model.quotationpackage;

import com.bloxico.ase.userservice.dto.entity.quotationpackage.QuotationPackageDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class ArrayQuotationPackageDataResponse {

    @JsonProperty("quotation_packages")
    @ApiModelProperty(required = true)
    List<QuotationPackageDto> quotationPackage;

}
