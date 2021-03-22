package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchEvaluableArtworksRequest {

    @NotNull
    @ApiParam(name = "countryId", required = true)
    Long countryId;

    @ApiParam(name = "title")
    String title;

    @ApiParam(name = "categories")
    List<String> categories;

}
