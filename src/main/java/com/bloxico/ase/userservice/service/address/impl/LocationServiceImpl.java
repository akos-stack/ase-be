package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.repository.address.*;
import com.bloxico.ase.userservice.service.address.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class LocationServiceImpl implements ILocationService {

    private final CountryRepository countryRepository;
    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;

    @Autowired
    public LocationServiceImpl(CountryRepository countryRepository,
                               LocationRepository locationRepository,
                               RegionRepository regionRepository)
    {
        this.countryRepository = countryRepository;
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public RegionDto findRegionByName(String region) {
        log.debug("LocationServiceImpl.findRegionByName - start | region: {}", region);
        var regionDto = regionRepository
                .findByNameIgnoreCase(region)
                .map(MAPPER::toDto)
                .orElseThrow(REGION_NOT_FOUND::newException);
        log.debug("LocationServiceImpl.findRegionByName - end | region: {}", region);
        return regionDto;
    }

    @Override
    public CountryDto findCountryByName(String country) {
        log.debug("LocationServiceImpl.findCountryByName - start | country: {}", country);
        var regionDto = countryRepository
                .findByNameIgnoreCase(country)
                .map(MAPPER::toDto)
                .orElseThrow(COUNTRY_NOT_FOUND::newException);
        log.debug("LocationServiceImpl.findCountryByName - end | country: {}", country);
        return regionDto;
    }


    @Override
    public RegionDto saveRegion(RegionDto dto, long principalId) {
        log.debug("LocationServiceImpl.saveRegion - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        requireNotExists(dto);
        var region = MAPPER.toEntity(dto);
        region.setCreatorId(principalId);
        region = regionRepository.saveAndFlush(region);
        var regionDto = MAPPER.toDto(region);
        log.debug("LocationServiceImpl.saveRegion - end | dto: {}, principalId: {}", dto, principalId);
        return regionDto;
    }

    @Override
    public CountryDto saveCountry(CountryDto dto, long principalId) {
        log.debug("LocationServiceImpl.saveCountry - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        requireNotExists(dto);
        var country = MAPPER.toEntity(dto);
        country.setCreatorId(principalId);
        country = countryRepository.saveAndFlush(country);
        var countryDto = MAPPER.toDto(country);
        log.debug("LocationServiceImpl.saveCountry - end | dto: {}, principalId: {}", dto, principalId);
        return countryDto;
    }

    @Override
    public LocationDto saveLocation(LocationDto dto, long principalId) {
        log.debug("LocationServiceImpl.saveLocation - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var location = MAPPER.toEntity(dto);
        location.setCreatorId(principalId);
        location = locationRepository.saveAndFlush(location);
        var locationDto = MAPPER.toDto(location);
        log.debug("LocationServiceImpl.saveLocation - end | dto: {}, principalId: {}", dto, principalId);
        return locationDto;
    }

    private void requireNotExists(RegionDto dto) {
        if (regionRepository.findByNameIgnoreCase(dto.getName()).isPresent())
            throw REGION_EXISTS.newException();
    }

    private void requireNotExists(CountryDto dto) {
        if (countryRepository.findByNameIgnoreCase(dto.getName()).isPresent())
            throw COUNTRY_EXISTS.newException();
    }

}
