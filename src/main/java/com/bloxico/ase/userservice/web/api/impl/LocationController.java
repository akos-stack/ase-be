package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import com.bloxico.ase.userservice.web.model.address.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SaveRegionResponse> saveRegion(SaveRegionRequest request) {
        var response = locationFacade.saveRegion(request);
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
    public ResponseEntity<SaveCountryResponse> saveCountry(SaveCountryRequest request) {
        var response = locationFacade.saveCountry(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UpdateCountryResponse> updateCountry(UpdateCountryRequest request) {
        var response = locationFacade.updateCountry(request);
        return ResponseEntity.ok(response);
    }

}
