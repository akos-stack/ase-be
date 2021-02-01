package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController implements LocationApi {

    @Autowired
    private ILocationFacade locationFacade;

    @Override
    public ResponseEntity<SearchCountriesResponse> findAllCountries() {
        return ResponseEntity.ok(locationFacade.findAllCountries());
    }

    @Override
    public ResponseEntity<SearchCitiesResponse> findAllCities() {
        return ResponseEntity.ok(locationFacade.findAllCities());
    }

}
