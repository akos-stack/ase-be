package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "countryId", callSuper = false)
public class CountryEvaluationDetailsDto extends BaseEntityDto {

    @JsonProperty("country_id")
    private Long countryId;

    @JsonProperty("price_per_evaluation")
    private Integer pricePerEvaluation;

    @JsonProperty("availability_percentage")
    private Integer availabilityPercentage;

}
