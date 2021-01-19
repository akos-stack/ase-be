package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.repository.address.*;
import com.bloxico.ase.userservice.service.address.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class LocationServiceImpl implements ILocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(CountryRepository countryRepository,
                               CityRepository cityRepository,
                               LocationRepository locationRepository)
    {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<CountryDto> allCountries() {
        log.debug("CountryServiceImpl.allCountries - start");
        var countries = countryRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .collect(toUnmodifiableList());
        log.debug("CountryServiceImpl.allCountries - end");
        return countries;
    }

    @Override
    public List<CityDto> allCities() {
        log.debug("LocationServiceImpl.allCities - start");
        var cities = cityRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .collect(toUnmodifiableList());
        log.debug("LocationServiceImpl.allCities - end");
        return cities;
    }

    @Override
    public CountryDto findOrSaveCountry(CountryDto dto, long principalId) {
        log.debug("CountryServiceImpl.findOrSaveCountry - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var countryDto = countryRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveCountry(dto, principalId));
        log.debug("CountryServiceImpl.findOrSaveCountry - end | dto: {}, principalId: {}", dto, principalId);
        return countryDto;
    }

    @Override
    public CityDto findOrSaveCity(CityDto dto, long principalId) {
        log.debug("LocationServiceImpl.findOrSaveCity - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var cityDto = cityRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveCity(dto, principalId));
        log.debug("LocationServiceImpl.findOrSaveCity - end | dto: {}, principalId: {}", dto, principalId);
        return cityDto;
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

    private CountryDto saveCountry(CountryDto dto, long principalId) {
        var country = MAPPER.toEntity(dto);
        country.setCreatorId(principalId);
        return MAPPER.toDto(countryRepository.saveAndFlush(country));
    }

    private CityDto saveCity(CityDto dto, long principalId) {
        var city = MAPPER.toEntity(dto);
        city.setCreatorId(principalId);
        return MAPPER.toDto(cityRepository.saveAndFlush(city));
    }

}
