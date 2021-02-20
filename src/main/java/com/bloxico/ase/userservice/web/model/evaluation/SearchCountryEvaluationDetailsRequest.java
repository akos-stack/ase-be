package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.validator.ValidSearchCountryEvaluationDetailsRequest;
import io.swagger.annotations.ApiParam;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidSearchCountryEvaluationDetailsRequest
public class SearchCountryEvaluationDetailsRequest {

    @ApiParam(value = "Narrows down search results to countries that are in given regions")
    Set<String> regions;

    @ApiParam(value = "Search term")
    String search = "";

    @ApiParam(value = "Page index", defaultValue = "0")
    int page = 0;

    @ApiParam(value = "Page size", defaultValue = "10")
    int size = 10;

    @ApiParam(value = "Sort by", defaultValue = "name")
    String sort = "country";

    @ApiParam(value = "Sort order", defaultValue = "asc")
    String order = "asc";

}
