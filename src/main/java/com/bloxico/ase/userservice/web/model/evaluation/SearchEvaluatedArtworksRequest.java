package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchEvaluatedArtworksRequest {

    @ApiParam(name = "artwork_title")
    private String artworkTitle;

    @Size(min = 1)
    @ApiParam(name = "categories")
    private List<String> categories;

}
