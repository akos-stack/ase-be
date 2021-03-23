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
public class SearchEvaluableArtworksRequest {

    @NotNull
    @ApiParam(name = "country_id", required = true)
    Long countryId;

    @ApiParam(name = "title")
    String title;

    @Size(min = 1)
    @ApiParam(name = "categories")
    List<String> categories;

}
