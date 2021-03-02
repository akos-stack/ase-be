package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.service.address.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
    public RegionDto findRegionById(Long id) {
        log.debug("LocationServiceImpl.findRegionById - start | id: {}", id);
        var regionDto = regionRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(REGION_NOT_FOUND::newException);
        log.debug("LocationServiceImpl.findRegionById - end | id: {}", id);
        return regionDto;
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
    public List<RegionDto> findAllRegionsWithNames(Collection<String> regionNames) {
        log.debug("LocationServiceImpl.findAllRegionsWithNames - start | regionNames: {}", regionNames);
        requireNonNull(regionNames);
        var regions = regionRepository
                .findAllByNameInIgnoreCase(regionNames)
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
        log.debug("LocationServiceImpl.findAllRegionsWithNames - end | regionNames: {}", regionNames);
        return regions;
    }

    @Override
    public CountryDto findCountryByName(String country) {
        log.debug("LocationServiceImpl.findCountryByName - start | country: {}", country);
        var countryDto = countryRepository
                .findByNameIgnoreCase(country)
                .map(MAPPER::toDto)
                .orElseThrow(COUNTRY_NOT_FOUND::newException);
        log.debug("LocationServiceImpl.findCountryByName - end | country: {}", country);
        return countryDto;
    }

    @Override
    public List<RegionDto> findAllRegions() {
        log.debug("LocationServiceImpl.findAllRegions - start");
        var regions = regionRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .sorted(comparing(RegionDto::getName))
                .collect(toList());
        log.debug("LocationServiceImpl.findAllRegions - end");
        return regions;
    }

    @Override
    public RegionDto saveRegion(RegionDto dto) {
        log.debug("LocationServiceImpl.saveRegion - start | dto: {}", dto);
        requireNonNull(dto);
        requireNotExists(dto);
        var region = MAPPER.toEntity(dto);
        region = regionRepository.saveAndFlush(region);
        var regionDto = MAPPER.toDto(region);
        log.debug("LocationServiceImpl.saveRegion - end | dto: {}", dto);
        return regionDto;
    }

    @Override
    public RegionDto deleteRegion(RegionDto dto) {
        log.debug("LocationServiceImpl.deleteRegion - start | dto: {}", dto);
        requireNonNull(dto);
        var region = MAPPER.toEntity(dto);
        regionRepository.delete(region);
        var regionDto = MAPPER.toDto(region);
        log.debug("LocationServiceImpl.deleteRegion - end | dto: {}", dto);
        return regionDto;
    }

    @Override
    public List<CountryDto> findAllCountries() {
        log.debug("LocationServiceImpl.findAllCountries - start");
        var countries = countryRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .sorted(comparing(CountryDto::getName))
                .collect(toList());
        log.debug("LocationServiceImpl.findAllCountries - end");
        return countries;
    }

    @Override
    public CountryDto saveCountry(CountryDto dto) {
        log.debug("LocationServiceImpl.saveCountry - start | dto: {}", dto);
        requireNonNull(dto);
        requireNotExists(dto);
        var country = MAPPER.toEntity(dto);
        country = countryRepository.saveAndFlush(country);
        var countryDto = MAPPER.toDto(country);
        log.debug("LocationServiceImpl.saveCountry - end | dto: {}", dto);
        return countryDto;
    }

    @Override
    public CountryDto updateCountry(CountryDto dto) {
        log.debug("LocationServiceImpl.updateCountry - start | dto: {}", dto);
        requireNonNull(dto);
        var country = countryRepository
                .findById(dto.getId())
                .orElseThrow(COUNTRY_NOT_FOUND::newException);
        if (countryNameUpdateRequested(country.getName(), dto.getName())) {
            requireNotExists(dto);
            country.setName(dto.getName());
        }
        var regions = dto.getRegions()
                .stream()
                .map(MAPPER::toEntity)
                .collect(toSet());
        country.setRegions(regions);
        country = countryRepository.saveAndFlush(country);
        var countryDto = MAPPER.toDto(country);
        log.debug("LocationServiceImpl.updateCountry - end | dto: {}", dto);
        return countryDto;
    }

    @Override
    public LocationDto saveLocation(LocationDto dto) {
        log.debug("LocationServiceImpl.saveLocation - start | dto: {}", dto);
        requireNonNull(dto);
        var location = MAPPER.toEntity(dto);
        location = locationRepository.saveAndFlush(location);
        var locationDto = MAPPER.toDto(location);
        log.debug("LocationServiceImpl.saveLocation - end | dto: {}", dto);
        return locationDto;
    }

    @Override
    public int countCountriesByRegionId(Long regionId) {
        log.debug("LocationServiceImpl.countCountriesByRegionId - start | regionId: {}", regionId);
        var count = countryRepository.countByRegionsIdEquals(regionId);
        log.debug("LocationServiceImpl.countCountriesByRegionId - end | regionId: {}", regionId);
        return count;
    }

    private void requireNotExists(RegionDto dto) {
        if (regionRepository.findByNameIgnoreCase(dto.getName()).isPresent())
            throw REGION_EXISTS.newException();
    }

    private void requireNotExists(CountryDto dto) {
        if (countryRepository.findByNameIgnoreCase(dto.getName()).isPresent())
            throw COUNTRY_EXISTS.newException();
    }

    private boolean countryNameUpdateRequested(String name, String updatedName) {
        return !name.equalsIgnoreCase(updatedName);
    }

}
