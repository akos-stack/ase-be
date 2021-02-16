package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.validator.ValidSearchRegionsRequest;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidSearchRegionsRequest
public class SearchRegionsRequest {

    @ApiParam(value = "Search term")
    private String search = "";

    @ApiParam(value = "Page index", defaultValue = "0")
    private int page = 0;

    @ApiParam(value = "Page size", defaultValue = "10")
    private int size = 10;

    @ApiParam(value = "Is pagination enabled", defaultValue = "false")
    private boolean paginated = false;

    @ApiParam(value = "Sort by", defaultValue = "name")
    private String sort = "name";

    @ApiParam(value = "Sort order", defaultValue = "asc")
    private String order = "asc";

}
