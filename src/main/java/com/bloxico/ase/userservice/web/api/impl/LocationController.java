package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.web.api.LocationApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController implements LocationApi {

    @Autowired
    private ILocationFacade locationFacade;

    @Override
    public ResponseEntity<List<CountryDto>> allCountries() {
        return ResponseEntity.ok(locationFacade.allCountries());
    }

    @Override
    public ResponseEntity<List<CityDto>> allCities() {
        return ResponseEntity.ok(locationFacade.allCities());
    }

}
