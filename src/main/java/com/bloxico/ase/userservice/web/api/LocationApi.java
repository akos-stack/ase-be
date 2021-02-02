package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.RegionDataResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "location")
public interface LocationApi {

    String COUNTRIES        = "/countries";
    String CITIES           = "/cities";
    String REGIONS_CREATE   = "/regions/create";

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
    ResponseEntity<RegionDataResponse> createRegion(
            @Valid @RequestBody CreateRegionRequest request, Principal principal);

}
