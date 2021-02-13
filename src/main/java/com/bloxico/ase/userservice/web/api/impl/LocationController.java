package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import com.bloxico.ase.userservice.web.model.address.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class LocationController implements LocationApi {

    @Autowired
    private ILocationFacade locationFacade;

    @Override
    public ResponseEntity<SearchCountriesResponse> findAllCountries(@Valid SearchCountriesRequest request) {
        return ResponseEntity.ok(locationFacade.findAllCountries(request));
    }

    @Override
    public ResponseEntity<SearchCitiesResponse> findAllCities() {
        return ResponseEntity.ok(locationFacade.findAllCities());
    }

    @Override
    public ResponseEntity<SearchRegionsResponse> findAllRegions(@Valid SearchRegionsRequest request) {
        return ResponseEntity.ok(locationFacade.findAllRegions(request));
    }

    @Override
    public ResponseEntity<RegionDataResponse> createRegion(
            @Valid @RequestBody CreateRegionRequest request, Principal principal) {

        var id = extractId(principal);
        var response = locationFacade.createRegion(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteRegion(@Valid @PathVariable("id") Integer regionId, Principal principal) {
        var id = extractId(principal);
        locationFacade.deleteRegion(regionId, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CountryDataResponse> createCountry(
            @Valid @RequestBody CreateCountryRequest request, Principal principal) {

        var id = extractId(principal);
        var response = locationFacade.createCountry(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> editCountry(
            @Valid @RequestBody EditCountryRequest request,
            @Valid @PathVariable("id") Integer countryId, Principal principal) {

        var id = extractId(principal);
        locationFacade.editCountry(request, countryId, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
