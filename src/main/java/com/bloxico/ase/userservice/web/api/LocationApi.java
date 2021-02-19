package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.address.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "location")
public interface LocationApi {

    String REGION_SAVE  =       "/region/save";
    String COUNTRY_SAVE =       "/country/save";
    String COUNTRY_UPDATE =     "/country/update/{id}";

    @PostMapping(
            value = REGION_SAVE,
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
            value = COUNTRY_SAVE,
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
            value = COUNTRY_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_country')")
    @ApiOperation(value = "Updates country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country successfully updated."),
            @ApiResponse(code = 409, message = "Country already exists."),
            @ApiResponse(code = 404, message = "Specified region doesn't exist.")
    })
    ResponseEntity<UpdateCountryResponse> updateCountry(
            @Valid @RequestBody UpdateCountryRequest request, @Valid @PathVariable("id") Integer countryId, Principal principal);

}
