package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.validator.ValidSearchRegionsRequest;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidSearchRegionsRequest
public class SearchRegionsRequest {

    @ApiParam(value = "Search term")
    String search = "";

    @ApiParam(value = "Page index", defaultValue = "0")
    int page = 0;

    @ApiParam(value = "Page size", defaultValue = "10")
    int size = 10;

    @ApiParam(value = "Sort by", defaultValue = "name")
    String sort = "name";

    @ApiParam(value = "Sort order", defaultValue = "asc")
    String order = "asc";

}

