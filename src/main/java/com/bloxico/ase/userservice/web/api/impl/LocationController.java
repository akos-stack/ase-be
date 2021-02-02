package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.RegionDataResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SearchCountriesResponse> findAllCountries() {
        return ResponseEntity.ok(locationFacade.findAllCountries());
    }

    @Override
    public ResponseEntity<SearchCitiesResponse> findAllCities() {
        return ResponseEntity.ok(locationFacade.findAllCities());
    }

    @Override
    public ResponseEntity<RegionDataResponse> createRegion(
            @Valid @RequestBody CreateRegionRequest request, Principal principal) {

        var id = extractId(principal);
        var response = locationFacade.createRegion(request, id);
        return ResponseEntity.ok(response);
    }

}
