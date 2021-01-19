package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Api(value = "location")
public interface LocationApi {

    String COUNTRIES = "/countries";
    String CITIES    = "/cities";

    @GetMapping(value = COUNTRIES)
    @ApiOperation(value = "Fetch all countries.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List of countries successfully retrieved.")
    })
    ResponseEntity<List<CountryDto>> allCountries();

    @GetMapping(value = CITIES)
    @ApiOperation(value = "Fetch all cities.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List of cities successfully retrieved.")
    })
    ResponseEntity<List<CityDto>> allCities();

}
