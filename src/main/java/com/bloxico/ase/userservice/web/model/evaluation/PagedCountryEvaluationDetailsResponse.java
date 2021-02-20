package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class PagedCountryEvaluationDetailsResponse {

    @JsonProperty("country_evaluation_details")
    @ApiModelProperty(required = true)
    List<CountryEvaluationDetailsCountedProj> countryEvaluationDetails;

    @JsonProperty("page_size")
    @ApiModelProperty(required = true)
    long pageElements;

    @JsonProperty("total_elements")
    @ApiModelProperty(required = true)
    long totalElements;

    @JsonProperty("total_pages")
    @ApiModelProperty(required = true)
    long totalPages;

}
