package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.address.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "location")
public interface LocationApi {

    String COUNTRIES        = "/countries";
    String COUNTRIES_CREATE = "/countries/create";
    String REGIONS_CREATE   = "/regions/create";

    @GetMapping(value = COUNTRIES)
    @ApiOperation(value = "Fetch all countries.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Countries successfully retrieved.")
    })
    ResponseEntity<SearchCountriesResponse> findAllCountries();

    @PostMapping(
            value = REGIONS_CREATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'create_region')")
    @ApiOperation(value = "Creates region in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Region successfully created."),
            @ApiResponse(code = 409, message = "Region already exists.")
    })
    ResponseEntity<RegionDataResponse> createRegion(@Valid @RequestBody CreateRegionRequest request, Principal principal);

    @PostMapping(
            value = COUNTRIES_CREATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'create_country')")
    @ApiOperation(value = "Creates country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country successfully created."),
            @ApiResponse(code = 409, message = "Country already exists."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist.")
    })
    ResponseEntity<CountryDataResponse> createCountry(@Valid @RequestBody CreateCountryRequest request, Principal principal);

}
