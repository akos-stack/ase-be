package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class LocationFacadeImpl implements ILocationFacade {

    private final ILocationService locationService;

    @Autowired
    public LocationFacadeImpl(ILocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public List<CountryDto> allCountries() {
        log.info("LocationFacadeImpl.allCountries - start");
        var countries = locationService.allCountries();
        log.info("LocationFacadeImpl.allCountries - end");
        return countries;
    }

    @Override
    public List<CityDto> allCities() {
        log.info("LocationFacadeImpl.allCities - start");
        var cities = locationService.allCities();
        log.info("LocationFacadeImpl.allCities - end");
        return cities;
    }

}
