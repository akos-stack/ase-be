package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(of = "artworkId", callSuper = false)
public class QuotationPackageDto extends BaseEntityDto {

    @JsonProperty("country_id")
    private Long artworkId;

    @JsonProperty("countries")
    private Set<QuotationPackageCountryDto> countries;

}
