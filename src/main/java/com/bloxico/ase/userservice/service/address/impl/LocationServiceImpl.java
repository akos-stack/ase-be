package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.bloxico.ase.userservice.repository.address.*;
import com.bloxico.ase.userservice.service.address.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class LocationServiceImpl implements ILocationService {

    private final CountryRepository countryRepository;
    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;
    private final CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Autowired
    public LocationServiceImpl(CountryRepository countryRepository,
                               LocationRepository locationRepository,
                               RegionRepository regionRepository,
                               CountryEvaluationDetailsRepository countryEvaluationDetailsRepository)
    {
        this.countryRepository = countryRepository;
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
        this.countryEvaluationDetailsRepository = countryEvaluationDetailsRepository;
    }

    @Override
    public List<CountryTotalOfEvaluatorsProj> findAllCountries() {
        log.debug("LocationServiceImpl.findAllCountries - start");
        var countries = countryRepository
                .findAllIncludeEvaluatorsCount()
                .stream()
                .collect(toUnmodifiableList());
        log.debug("LocationServiceImpl.findAllCountries - end");
        return countries;
    }

    @Override
    public CountryDto findOrSaveCountry(CountryDto dto, long principalId) {
        log.debug("LocationServiceImpl.findOrSaveCountry - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var countryDto = countryRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveCountry(dto, principalId));
        log.debug("LocationServiceImpl.findOrSaveCountry - end | dto: {}, principalId: {}", dto, principalId);
        return countryDto;
    }

    @Override
    public LocationDto saveLocation(LocationDto dto, long principalId) {
        log.debug("LocationServiceImpl.saveLocation - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var location = MAPPER.toEntity(dto);
        location.setCreatorId(principalId);
        var locationDto = MAPPER.toDto(locationRepository.saveAndFlush(location));
        log.debug("LocationServiceImpl.saveLocation - end | dto: {}, principalId: {}", dto, principalId);
        return locationDto;
    }

    @Override
    public RegionDto createRegion(RegionDto dto, long principalId) {
        log.debug("LocationServiceImpl.createRegion - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        if (regionAlreadyExists(dto.getName())) {
            throw REGION_EXISTS.newException();
        }
        var region = MAPPER.toEntity(dto);
        region.setCreatorId(principalId);
        regionRepository.saveAndFlush(region);
        var regionDto = MAPPER.toDto(region);
        regionDto.setNumberOfCountries(0);
        regionDto.setNumberOfEvaluators(0);
        log.debug("LocationServiceImpl.createRegion - end | dto: {}, principalId: {}", dto, principalId);
        return regionDto;
    }

    @Override
    public CountryDto createCountry(CountryDto dto, long principalId) {
        log.debug("LocationServiceImpl.createCountry - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        if (countryAlreadyExists(dto.getName())) {
            throw COUNTRY_EXISTS.newException();
        }
        var region = regionRepository
                .findByNameIgnoreCase(dto.getRegion().getName())
                .orElseThrow(REGION_NOT_FOUND::newException);
        var country = MAPPER.toEntity(dto);
        country.setCreatorId(principalId);
        country.setRegion(region);
        countryRepository.saveAndFlush(country);
        var countryDto = MAPPER.toDto(country);
        log.debug("LocationServiceImpl.createCountry - end | dto: {}, principalId: {}", dto, principalId);
        return countryDto;
    }

    @Override
    public CountryEvaluationDetailsDto createCountryEvaluationDetails(
            CountryEvaluationDetailsDto dto, int countryId, long principalId) {
        log.debug("LocationServiceImpl.createCountryEvaluationDetails - start | dto: {}, countryId: {}, principalId: {}",
                dto, countryId, principalId);
        requireNonNull(dto);
        var country = countryRepository
                .findById(countryId)
                .orElseThrow(COUNTRY_NOT_FOUND::newException);
        var evaluationDetails = MAPPER.toEntity(dto);
        evaluationDetails.setCreatorId(principalId);
        evaluationDetails.setCountry(country);
        countryEvaluationDetailsRepository.saveAndFlush(evaluationDetails);
        var evaluationDetailsDto = MAPPER.toDto(evaluationDetails);
        log.debug("LocationServiceImpl.createCountryEvaluationDetails - end | dto: {}, countryId: {}, principalId: {}",
                dto, countryId, principalId);
        return evaluationDetailsDto;
    }

    private CountryDto saveCountry(CountryDto dto, long principalId) {
        var country = MAPPER.toEntity(dto);
        country.setCreatorId(principalId);
        return MAPPER.toDto(countryRepository.saveAndFlush(country));
    }

    private boolean regionAlreadyExists(String name) {
        return regionRepository
                .findByNameIgnoreCase(name)
                .isPresent();
    }

    private boolean countryAlreadyExists(String name) {
        return countryRepository
                .findByNameIgnoreCase(name)
                .isPresent();
    }

}
