package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchCountryEvaluationDetailsRequest implements ISearchCountryEvaluationDetailsRequest {

    @NotNull
    @ApiParam(name = "search", required = true)
    String search;

    @Size(min = 1)
    @ApiParam(name = "regions")
    List<String> regions;

}
