package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import com.bloxico.ase.userservice.web.model.address.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class LocationController implements LocationApi {

    @Autowired
    private ILocationFacade locationFacade;

    @Override
    public ResponseEntity<SearchRegionsResponse> findAllRegions() {
        var response = locationFacade.findAllRegions();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveRegionResponse> saveRegion(SaveRegionRequest request, Principal principal) {
        var id = extractId(principal);
        var response = locationFacade.saveRegion(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteRegion(DeleteRegionRequest request) {
        locationFacade.deleteRegion(request.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SearchCountriesResponse> findAllCountries() {
        var response = locationFacade.findAllCountries();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveCountryResponse> saveCountry(SaveCountryRequest request, Principal principal) {
        var id = extractId(principal);
        var response = locationFacade.saveCountry(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UpdateCountryResponse> updateCountry(UpdateCountryRequest request, Principal principal) {
        var id = extractId(principal);
        var response = locationFacade.updateCountry(request, id);
        return ResponseEntity.ok(response);
    }

}
