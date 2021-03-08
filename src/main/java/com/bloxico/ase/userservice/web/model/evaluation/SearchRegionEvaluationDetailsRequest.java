package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchRegionEvaluationDetailsRequest {

    @NotNull
    @ApiParam(name = "search", required = true)
    String search;

}
