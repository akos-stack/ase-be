package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.web.model.address.*;
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
    public SaveRegionResponse saveRegion(SaveRegionRequest request, long principalId) {
        log.debug("LocationFacadeImpl.saveRegion - start | request: {}, principalId: {}", request, principalId);
        var regionDto = MAPPER.toRegionDto(request);
        regionDto = locationService.saveRegion(regionDto, principalId);
        var response = new SaveRegionResponse(regionDto);
        log.debug("LocationFacadeImpl.saveRegion - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public SaveCountryResponse saveCountry(SaveCountryRequest request, long principalId) {
        log.debug("LocationFacadeImpl.saveCountry - start | request: {}, principalId: {}", request, principalId);
        var countryDto = MAPPER.toCountryDto(request);
        countryDto.setRegion(locationService.findRegionByName(request.getRegion()));
        countryDto = locationService.saveCountry(countryDto, principalId);
        var response = new SaveCountryResponse(countryDto);
        log.debug("LocationFacadeImpl.saveCountry - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

}
