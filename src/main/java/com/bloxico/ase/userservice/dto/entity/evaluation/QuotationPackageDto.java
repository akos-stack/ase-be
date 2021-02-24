package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(of = "artworkId")
public class QuotationPackageDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("country_id")
    private Integer artworkId;

    @JsonProperty("countries")
    private Set<QuotationPackageCountryDto> countries;

}
