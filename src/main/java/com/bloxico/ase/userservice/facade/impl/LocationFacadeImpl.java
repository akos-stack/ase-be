package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.RegionDataResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

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
    public SearchCountriesResponse findAllCountries() {
        log.info("LocationFacadeImpl.findAllCountries - start");
        var countries = locationService.findAllCountries();
        var response = new SearchCountriesResponse(countries);
        log.info("LocationFacadeImpl.findAllCountries - end");
        return response;
    }

    @Override
    public SearchCitiesResponse findAllCities() {
        log.info("LocationFacadeImpl.findAllCities - start");
        var cities = locationService.findAllCities();
        var response = new SearchCitiesResponse(cities);
        log.info("LocationFacadeImpl.findAllCities - end");
        return response;
    }

    @Override
    public RegionDataResponse createRegion(CreateRegionRequest request, long principalId) {
        log.debug("LocationFacadeImpl.createRegion - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toDto(request);
        dto = locationService.createRegion(dto, principalId);
        log.debug("LocationFacadeImpl.createRegion - end | request: {}, principalId: {}", request, principalId);
        return new RegionDataResponse(dto);
    }

}
