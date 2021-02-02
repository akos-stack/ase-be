package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.address.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@Transactional
public class LocationFacadeImpl implements ILocationFacade {

    private final ILocationService locationService;
    private final IUserProfileService userProfileService;

    @Autowired
    public LocationFacadeImpl(ILocationService locationService,
                              IUserProfileService userProfileService) {
        this.locationService = locationService;
        this.userProfileService = userProfileService;
    }

    @Override
    public SearchCountriesResponse findAllCountries() {
        log.info("LocationFacadeImpl.findAllCountries - start");
        var countries = locationService.findAllCountries();
        userProfileService.fetchTotalOfEvaluatorsForCountries(countries);
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
        requireNonNull(request);
        var dto = MAPPER.toRegionDto(request);
        dto = locationService.createRegion(dto, principalId);
        log.debug("LocationFacadeImpl.createRegion - end | request: {}, principalId: {}", request, principalId);
        return new RegionDataResponse(dto);
    }

    @Override
    public CountryDataResponse createCountry(CreateCountryRequest request, long principalId) {
        log.debug("LocationFacadeImpl.createCountry - start | request: {}, principalId: {}", request, principalId);
        requireNonNull(request);
        var dto = MAPPER.toDto(request);
        var countryDto = locationService.createCountry(dto, principalId);
        log.debug("LocationFacadeImpl.createCountry - end | request: {}, principalId: {}", request, principalId);
        return new CountryDataResponse(countryDto);
    }

}
