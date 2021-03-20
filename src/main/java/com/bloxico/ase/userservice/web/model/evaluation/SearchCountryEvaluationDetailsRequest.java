package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchCountryEvaluationDetailsRequest implements ISearchCountryEvaluationDetailsRequest {

    @NotNull
    @ApiParam(name = "search", required = true)
    private String search;

    @Size(min = 1)
    @ApiParam(name = "regions")
    private List<String> regions;

}
