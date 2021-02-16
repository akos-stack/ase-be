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

    String COUNTRIES            = "/countries";
    String COUNTRIES_CREATE     = "/countries/create";
    String COUNTRIES_EDIT       = "/countries/edit/{id}";
    String CITIES               = "/cities";
    String REGIONS              = "/regions";
    String REGIONS_CREATE       = "/regions/create";
    String REGIONS_DELETE       = "/regions/delete/{id}";

    @GetMapping(value = COUNTRIES)
    @ApiOperation(value = "Search countries.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Countries successfully retrieved.")
    })
    ResponseEntity<SearchCountriesResponse> searchCountries(@Valid SearchCountriesRequest request);

    @GetMapping(value = CITIES)
    @ApiOperation(value = "Fetch all cities.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cities successfully retrieved.")
    })
    ResponseEntity<SearchCitiesResponse> findAllCities();

    @GetMapping(value = REGIONS)
    @ApiOperation(value = "Search regions.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Regions successfully retrieved.")
    })
    ResponseEntity<SearchRegionsResponse> searchRegions(@Valid SearchRegionsRequest request);

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

    @DeleteMapping(value = REGIONS_DELETE)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_region')")
    @ApiOperation(value = "Deletes region from the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Region successfully deleted."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist."),
            @ApiResponse(code = 400, message = "Region has one or more countries tied down to it.")
    })
    ResponseEntity<Void> deleteRegion(
            @Valid @PathVariable("id") Integer regionId, Principal principal);

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
    ResponseEntity<CountryDataResponse> createCountry(
            @Valid @RequestBody CreateCountryRequest request, Principal principal);

    @PutMapping(
            value = COUNTRIES_EDIT,
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_country')")
    @ApiOperation(value = "Edits country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country successfully edited."),
            @ApiResponse(code = 409, message = "Country already exists."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist."),
            @ApiResponse(code = 404, message = "Specified country doesn't exist.")
    })
    ResponseEntity<Void> editCountry(
            @Valid @RequestBody EditCountryRequest request,
            @Valid @PathVariable("id") Integer countryId, Principal principal);

}
