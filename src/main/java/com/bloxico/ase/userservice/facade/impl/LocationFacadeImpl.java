package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.web.model.address.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.REGION_DELETE_OPERATION_NOT_SUPPORTED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.REGION_NOT_FOUND;

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
        var regionDto = doSaveRegion(request, principalId);
        var response = new SaveRegionResponse(regionDto);
        log.debug("LocationFacadeImpl.saveRegion - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void deleteRegion(int regionId) {
        log.debug("LocationFacadeImpl.deleteRegion - start | regionId: {}", regionId);
        doDeleteRegion(regionId);
        log.debug("LocationFacadeImpl.deleteRegion - end | regionId: {}", regionId);
    }

    @Override
    public SearchCountriesResponse findAllCountries() {
        log.debug("LocationFacadeImpl.findAllCountries - start");
        var countries = locationService.findAllCountries();
        var response = new SearchCountriesResponse(countries);
        log.debug("LocationFacadeImpl.findAllCountries - end");
        return response;
    }

    @Override
    public SaveCountryResponse saveCountry(SaveCountryRequest request, long principalId) {
        log.debug("LocationFacadeImpl.saveCountry - start | request: {}, principalId: {}", request, principalId);
        var countryDto = doSaveCountry(request, principalId);
        var response = new SaveCountryResponse(countryDto);
        log.debug("LocationFacadeImpl.saveCountry - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public UpdateCountryResponse updateCountry(UpdateCountryRequest request, long principalId) {
        log.debug("LocationFacadeImpl.updateCountry - start | request: {}, principalId: {}", request, principalId);
        var countryDto = doUpdateCountry(request, principalId);
        var response = new UpdateCountryResponse(countryDto);
        log.debug("LocationFacadeImpl.updateCountry - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    private RegionDto doSaveRegion(SaveRegionRequest request, long principalId) {
        var regionDto = MAPPER.toRegionDto(request);
        return locationService.saveRegion(regionDto, principalId);
    }

    private void doDeleteRegion(int regionId) {
        var regionDto = locationService.findRegionById(regionId);
        requireRegionHasNoCountries(regionDto);
        locationService.deleteRegion(regionDto);
    }

    private void requireRegionHasNoCountries(RegionDto dto) {
        var countriesInRegion = locationService.countCountriesByRegionId(dto.getId());
        if (countriesInRegion > 0)
            throw REGION_DELETE_OPERATION_NOT_SUPPORTED.newException();
    }

    private List<RegionDto> findAllRegionsByNames(Collection<String> names) {
        var regions = locationService.findAllRegionsWithNames(names);
        if (regions.size() != names.size())
            throw REGION_NOT_FOUND.newException();
        return regions;
    }

    private CountryDto doSaveCountry(SaveCountryRequest request, long principalId) {
        var countryDto = MAPPER.toCountryDto(request);
        countryDto.addAllRegions(findAllRegionsByNames(request.getRegions()));
        return locationService.saveCountry(countryDto, principalId);
    }

    private CountryDto doUpdateCountry(UpdateCountryRequest request, long principalId) {
        var countryDto = MAPPER.toCountryDto(request);
        countryDto.addAllRegions(findAllRegionsByNames(request.getRegions()));
        return locationService.updateCountry(countryDto, principalId);
    }

}
