package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.address.*;
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

    String REGIONS                   = "/regions";
    String REGION_MANAGEMENT_SAVE    = "/region/management/save";
    String REGION_MANAGEMENT_DELETE  = "/region/management/delete";
    String COUNTRIES                 = "/countries";
    String COUNTRY_MANAGEMENT_SAVE   = "/country/management/save";
    String COUNTRY_MANAGEMENT_UPDATE = "/country/management/update";

    @GetMapping(
            value = REGIONS,
            produces = "application/json")
    @ApiOperation(value = "Fetches all regions from the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Regions successfully fetched.")
    })
    ResponseEntity<SearchRegionsResponse> findAllRegions();

    @PostMapping(
            value = REGION_MANAGEMENT_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_region')")
    @ApiOperation(value = "Saves region in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Region successfully saved."),
            @ApiResponse(code = 409, message = "Region already exists.")
    })
    ResponseEntity<SaveRegionResponse> saveRegion(@Valid @RequestBody SaveRegionRequest request, Principal principal);

    @PostMapping(
            value = REGION_MANAGEMENT_DELETE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_region')")
    @ApiOperation(value = "Deletes region in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Region successfully deleted."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist."),
            @ApiResponse(code = 409, message = "Region has one or more countries tied down to it.")
    })
    ResponseEntity<Void> deleteRegion(@Valid @RequestBody DeleteRegionRequest request);

    @GetMapping(
            value = COUNTRIES,
            produces = { "application/json" })
    @ApiOperation(value = "Fetches all countries from the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Countries successfully fetched.")
    })
    ResponseEntity<SearchCountriesResponse> findAllCountries();

    @PostMapping(
            value = COUNTRY_MANAGEMENT_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_country')")
    @ApiOperation(value = "Saves country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country successfully saved."),
            @ApiResponse(code = 409, message = "Country already exists."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist.")
    })
    ResponseEntity<SaveCountryResponse> saveCountry(@Valid @RequestBody SaveCountryRequest request, Principal principal);

    @PostMapping(
            value = COUNTRY_MANAGEMENT_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_country')")
    @ApiOperation(value = "Updates country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country successfully updated."),
            @ApiResponse(code = 409, message = "Country already exists."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist."),
            @ApiResponse(code = 404, message = "Specified country doesn't exist.")
    })
    ResponseEntity<UpdateCountryResponse> updateCountry(@Valid @RequestBody UpdateCountryRequest request, Principal principal);

}
