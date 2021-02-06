package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationServiceImpl service;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Test
    public void findAllCountries() {
        assertEquals(
                List.of(),
                service.findAllCountries());
        var country1 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1),
                service.findAllCountries());
        var country2 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1, country2),
                service.findAllCountries());
    }

    @Test
    public void findAllCities() {
        assertEquals(
                List.of(),
                service.findAllCities());
        var city1 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1),
                service.findAllCities());
        var city2 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1, city2),
                service.findAllCities());
    }

    @Test(expected = NullPointerException.class)
    public void findOrSaveCountry_nullCountry() {
        service.findOrSaveCountry(null, 1);
    }

    @Test
    public void findOrSaveCountry_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = new CountryDto();
        country.setName(uuid());
        assertEquals(List.of(), service.findAllCountries());
        service.findOrSaveCountry(country, principalId);
        assertEquals(List.of(country), service.findAllCountries());
    }

    @Test
    public void findOrSaveCountry_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = mockUtil.savedCountryDto();
        assertEquals(List.of(country), service.findAllCountries());
        assertEquals(country, service.findOrSaveCountry(country, principalId));
        assertEquals(List.of(country), service.findAllCountries());
    }

    @Test(expected = NullPointerException.class)
    public void findOrSaveCity_nullCity() {
        service.findOrSaveCity(null, 1);
    }

    @Test
    public void findOrSaveCity_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = new CityDto();
        city.setCountry(mockUtil.savedCountryDto());
        city.setName(uuid());
        assertEquals(List.of(), service.findAllCities());
        service.findOrSaveCity(city, principalId);
        assertEquals(List.of(city), service.findAllCities());
    }

    @Test
    public void findOrSaveCity_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = mockUtil.savedCityDto();
        assertEquals(List.of(city), service.findAllCities());
        assertEquals(city, service.findOrSaveCity(city, principalId));
        assertEquals(List.of(city), service.findAllCities());
    }

    @Test(expected = NullPointerException.class)
    public void saveLocation_nullLocation() {
        service.saveLocation(null, 1);
    }

    @Test
    public void saveLocation() {
        var principalId = mockUtil.savedAdmin().getId();
        var location = new LocationDto();
        location.setCity(mockUtil.savedCityDto());
        location.setAddress(uuid());
        assertEquals(List.of(), repository.findAll());
        service.saveLocation(location, principalId);
        assertEquals(
                List.of(location),
                repository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()));
    }

    @Test(expected = NullPointerException.class)
    public void createRegion_nullRegion() {
        service.createRegion(null, 1);
    }

    @Test(expected = LocationException.class)
    public void createRegion_regionAlreadyExists() {
        var dto = mockUtil.genRegionDto();
        service.createRegion(dto, 1);
        service.createRegion(dto, 1);
    }

    @Test
    public void createRegion_regionSuccessfullyCreated() {
        var principalId = mockUtil.savedAdmin().getId();
        var regionName = uuid();
        var dto = new RegionDto();
        dto.setName(regionName);

        var regionDto = service.createRegion(dto, principalId);
        var newlyCreatedRegion = regionRepository
                .findByNameIgnoreCase(regionName)
                .orElse(null);

        assertNotNull(newlyCreatedRegion);
        assertEquals(regionDto.getId(), newlyCreatedRegion.getId());
        assertEquals(principalId, newlyCreatedRegion.getCreatorId());
        assertEquals(regionName, newlyCreatedRegion.getName());
        assertEquals(0, regionDto.getNumberOfCountries().intValue());
        assertEquals(0, regionDto.getNumberOfEvaluators().intValue());
    }

    @Test(expected = NullPointerException.class)
    public void createCountry_nullCountry() {
        service.createCountry(null, 1);
    }

    @Test(expected = LocationException.class)
    public void createCountry_countryAlreadyExists() {
        var countryDto = mockUtil.genCountryDto(mockUtil.savedRegionDto());
        service.createCountry(countryDto, 1);
        service.createCountry(countryDto, 1);
    }

    @Test(expected = LocationException.class)
    public void createCountry_regionNotFound() {
        var countryDto = mockUtil.genCountryDto(mockUtil.genRegionDto());
        service.createCountry(countryDto, 1);
    }

    @Test
    public void createCountry_countrySuccessfullyCreated() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.genCountryDto(mockUtil.savedRegionDto());
        var countryDto = service.createCountry(dto, principalId);
        var newlyCreatedCountry = countryRepository
                .findByNameIgnoreCase(countryDto.getName())
                .orElse(null);

        assertNotNull(newlyCreatedCountry);
        assertEquals(countryDto.getRegion().getName(), newlyCreatedCountry.getRegion().getName());
        assertEquals(countryDto.getId(), newlyCreatedCountry.getId());
        assertEquals(countryDto.getName(), newlyCreatedCountry.getName());
        assertEquals(principalId, newlyCreatedCountry.getCreatorId());
    }

    @Test(expected = NullPointerException.class)
    public void createCountryEvaluationDetails_nullEvaluationDetails() {
        var countryId = mockUtil.savedCountry().getId();
        service.createCountryEvaluationDetails(null, countryId, 1);
    }

    @Test(expected = LocationException.class)
    public void createCountryEvaluationDetails_countryNotFound() {
        var evaluationDetailsDto = mockUtil.genCountryEvaluationDetailsDto();
        service.createCountryEvaluationDetails(evaluationDetailsDto, -1, 1);
    }

    @Test
    public void createCountryEvaluationDetails_evaluationDetailsSuccessfullyCreated() {
        var adminId = mockUtil.savedAdmin().getId();
        var countryDto = mockUtil.genCountryDto(mockUtil.savedRegionDto());
        countryDto = service.createCountry(countryDto, adminId);
        var evaluationDetailsDto = mockUtil.genCountryEvaluationDetailsDto();
        evaluationDetailsDto =
                service.createCountryEvaluationDetails(evaluationDetailsDto, countryDto.getId(), adminId);

        var newlyCreatedEvaluationDetails = countryEvaluationDetailsRepository
                .findById(evaluationDetailsDto.getId())
                .orElse(null);

        assertNotNull(newlyCreatedEvaluationDetails);
        assertEquals(countryDto.getId(), newlyCreatedEvaluationDetails.getCountry().getId());
        assertEquals(evaluationDetailsDto.getPricePerEvaluation(), newlyCreatedEvaluationDetails.getPricePerEvaluation());
        assertEquals(evaluationDetailsDto.getAvailabilityPercentage(), newlyCreatedEvaluationDetails.getAvailabilityPercentage());
        assertEquals(adminId, newlyCreatedEvaluationDetails.getCreatorId());
    }

}
