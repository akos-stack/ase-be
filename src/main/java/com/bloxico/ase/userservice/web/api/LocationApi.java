package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Api(value = "location")
public interface LocationApi {

    String COUNTRIES = "/countries";
    String CITIES    = "/cities";

    @GetMapping(value = COUNTRIES)
    @ApiOperation(value = "Fetch all countries.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Countries successfully retrieved.")
    })
    ResponseEntity<SearchCountriesResponse> findAllCountries();

    @GetMapping(value = CITIES)
    @ApiOperation(value = "Fetch all cities.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cities successfully retrieved.")
    })
    ResponseEntity<SearchCitiesResponse> findAllCities();

}
