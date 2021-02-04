package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.repository.address.CityRepository;
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
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class LocationServiceImpl implements ILocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;

    @Autowired
    public LocationServiceImpl(CountryRepository countryRepository,
                               CityRepository cityRepository,
                               LocationRepository locationRepository,
                               RegionRepository regionRepository)
    {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public List<CountryDto> findAllCountries() {
        log.debug("CountryServiceImpl.findAllCountries - start");
        var countries = countryRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .collect(toUnmodifiableList());
        fetchTotalOfEvaluatorsForCountries(countries);
        log.debug("CountryServiceImpl.findAllCountries - end");
        return countries;
    }

    @Override
    public List<CityDto> findAllCities() {
        log.debug("LocationServiceImpl.findAllCities - start");
        var cities = cityRepository
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .collect(toUnmodifiableList());
        log.debug("LocationServiceImpl.findAllCities - end");
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

    @Override
    public RegionDto createRegion(RegionDto dto, long principalId) {
        log.debug("LocationServiceImpl.createRegion - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        if (regionAlreadyExists(dto.getName())) {
            throw REGION_EXISTS.newException();
        }
        var region = MAPPER.toEntity(dto);
        region.setCreatorId(principalId);
        region = regionRepository.saveAndFlush(region);
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
        var evaluationDetails = MAPPER.toEntity(dto.getCountryEvaluationDetails());
        var country = MAPPER.toEntity(dto);
        country.setCreatorId(principalId);
        country.setRegion(region);
        country.setCountryEvaluationDetails(evaluationDetails);
        evaluationDetails.setCountry(country);
        countryRepository.saveAndFlush(country);
        var countryDto = MAPPER.toDto(country);
        countryDto.getCountryEvaluationDetails().setTotalOfEvaluators(0);
        log.debug("LocationServiceImpl.createCountry - end | dto: {}, principalId: {}", dto, principalId);
        return countryDto;
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

    private void fetchTotalOfEvaluatorsForCountries(Collection<CountryDto> countries) {
        if (countries.isEmpty()) {
            return;
        }
        var countriesMap = countries.stream()
                .collect(toMap(CountryDto::getId, country -> country));
        var pairs = countryRepository
                .countTotalOfEvaluatorsByIdIn(countriesMap.keySet());
        pairs
                .forEach(p -> {
                    var countryId = p.getCountryId();
                    var totalOfEvaluators = p.getTotalOfEvaluators();
                    countriesMap.get(countryId)
                            .getCountryEvaluationDetails()
                            .setTotalOfEvaluators(totalOfEvaluators);
                });
    }

}
