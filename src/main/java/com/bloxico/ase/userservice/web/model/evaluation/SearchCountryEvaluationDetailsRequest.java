package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchCountryEvaluationDetailsRequest implements ISearchCountryEvaluationDetailsRequest {

    @NotNull
    @ApiParam(name = "search", required = true)
    String search;

    @ApiParam(name = "regions")
    List<String> regions;

}
