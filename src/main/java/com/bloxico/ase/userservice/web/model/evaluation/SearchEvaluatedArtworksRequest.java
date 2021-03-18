package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchEvaluatedArtworksRequest {

    @ApiParam(name = "artName")
    String artName;

    @Size(min = 1)
    @ApiParam(name = "categories")
    private List<String> categories;

}
