package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.facade.ILocationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
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

    @Autowired
    public LocationFacadeImpl(ILocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public SearchCountriesResponse findAllCountries(SearchCountriesRequest request) {
        log.info("LocationFacadeImpl.findAllCountries - start | request: {}", request);
        var page = locationService.findAllCountries(request);
        var response = new SearchCountriesResponse(page.getContent(), page.getTotalElements());
        log.info("LocationFacadeImpl.findAllCountries - end | request: {}", request);
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
    public SearchRegionsResponse searchRegions(SearchRegionsRequest request) {
        log.info("LocationFacadeImpl.searchRegions - start | request: {}", request);
        requireNonNull(request);
        var page = locationService.findAllRegions(request);
        var response = new SearchRegionsResponse(page.getContent(), page.getTotalElements());
        log.info("LocationFacadeImpl.searchRegions - end | request: {}", request);
        return response;
    }

    @Override
    public RegionDataResponse createRegion(CreateRegionRequest request, long principalId) {
        log.debug("LocationFacadeImpl.createRegion - start | request: {}, principalId: {}", request, principalId);
        requireNonNull(request);
        var dto = MAPPER.toRegionDto(request);
        var regionDto= locationService.createRegion(dto, principalId);
        log.debug("LocationFacadeImpl.createRegion - end | request: {}, principalId: {}", request, principalId);
        return new RegionDataResponse(regionDto);
    }

    @Override
    public void deleteRegion(int regionId, long principalId) {
        log.debug("LocationFacadeImpl.deleteRegion - start | regionId: {}, principalId: {}", regionId, principalId);
        locationService.deleteRegion(regionId, principalId);
        log.debug("LocationFacadeImpl.deleteRegion - end | regionId: {}, principalId: {}", regionId, principalId);
    }

    @Override
    public CountryDataResponse createCountry(CreateCountryRequest request, long principalId) {
        log.debug("LocationFacadeImpl.createCountry - start | request: {}, principalId: {}", request, principalId);
        requireNonNull(request);
        var countryDto= doCreateCountry(request, principalId);
        log.debug("LocationFacadeImpl.createCountry - end | request: {}, principalId: {}", request, principalId);
        return new CountryDataResponse(countryDto);
    }

    @Override
    public void editCountry(EditCountryRequest request, int countryId, long principalId) {
        log.debug("LocationFacadeImpl.editCountry - start | request: {}, countryId: {}, principalId: {}",
                request, countryId, principalId);
        requireNonNull(request);
        doEditCountry(request, countryId, principalId);
        log.debug("LocationFacadeImpl.editCountry - end | request: {}, countryId: {}, principalId: {}",
                request, countryId, principalId);
    }

    private CountryDto doCreateCountry(CreateCountryRequest request, long principalId) {
        var dto = MAPPER.toCountryDto(request);
        var countryDto = locationService.createCountry(dto, principalId);
        var evaluationDetailsDto= doCreateCountryEvaluationDetails(request, countryDto.getId(), principalId);
        countryDto.setCountryEvaluationDetails(evaluationDetailsDto);
        return countryDto;
    }

    private CountryEvaluationDetailsDto doCreateCountryEvaluationDetails(
            CreateCountryRequest request, int countryId, long principalId) {
        var dto = MAPPER.toCountryEvaluationDetailsDto(request);
        return locationService.createCountryEvaluationDetails(dto, countryId, principalId);
    }

    private void doEditCountry(EditCountryRequest request, int countryId, long principalId) {
        var dto = MAPPER.toCountryDto(request);
        dto = locationService.editCountry(dto, countryId, principalId);
        var evaluationDetailsChangeRequested =
                request.getPricePerEvaluation() != dto.getCountryEvaluationDetails().getPricePerEvaluation()
                        || request.getAvailabilityPercentage() != dto.getCountryEvaluationDetails().getAvailabilityPercentage();
        if (!evaluationDetailsChangeRequested) {
            return;
        }
        doEditCountryEvaluationDetails(request, countryId, principalId);
    }

    private void doEditCountryEvaluationDetails(EditCountryRequest request, int countryId, long principalId) {
        var dto = MAPPER.toCountryEvaluationDetailsDto(request);
        locationService.editCountryEvaluationDetails(dto, countryId, principalId);
    }

}
