package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.web.model.address.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.REGION_DELETE_OPERATION_NOT_SUPPORTED;

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
    public SearchRegionsResponse findAllRegions() {
        log.debug("LocationFacadeImpl.findAllRegions - start");
        var regions = locationService.findAllRegions();
        var response = new SearchRegionsResponse(regions);
        log.debug("LocationFacadeImpl.findAllRegions - end");
        return response;
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
    public void deleteRegion(int regionId) {
        log.debug("LocationFacadeImpl.deleteRegion - start | regionId: {}", regionId);
        var regionDto = locationService.findRegionById(regionId);
        requireRegionHasNoCountries(regionDto);
        locationService.deleteRegion(regionDto);
        log.debug("LocationFacadeImpl.deleteRegion - end | regionId: {}", regionId);
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

    @Override
    public UpdateCountryResponse updateCountry(UpdateCountryRequest request, int countryId, long principalId) {
        log.debug("LocationFacadeImpl.updateCountry - start | request: {}, countryId: {}, principalId: {}",
                request, countryId, principalId);
        var countryDto = MAPPER.toCountryDto(request);
        countryDto.setId(countryId);
        countryDto.setRegion(locationService.findRegionByName(request.getRegion()));
        countryDto = locationService.updateCountry(countryDto, principalId);
        var response = new UpdateCountryResponse(countryDto);
        log.debug("LocationFacadeImpl.updateCountry - end | request: {}, countryId: {}, principalId: {}",
                request, countryId, principalId);
        return response;
    }

    private void requireRegionHasNoCountries(RegionDto dto) {
        var countriesInRegion = locationService.countCountriesByRegionId(dto.getId());
        if (countriesInRegion > 0) throw REGION_DELETE_OPERATION_NOT_SUPPORTED.newException();
    }

}
