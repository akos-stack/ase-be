package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveQuotationPackageRequest {

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true, access = PRIVATE)
    public static class Country {

        @NotNull
        @JsonProperty("country_id")
        @ApiModelProperty(required = true)
        Long countryId;

        @NotNull
        @Min(1)
        @JsonProperty("number_of_evaluations")
        @ApiModelProperty(required = true)
        Integer numberOfEvaluations;

    }

    @NotNull
    @JsonProperty("country_id")
    Long artworkId;

    @NotNull
    @JsonProperty("countries")
    @ApiModelProperty(required = true)
    Set<Country> countries;

}
