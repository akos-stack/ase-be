package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private LocationRepository locationRepository;
    @Autowired private LocationServiceImpl service;
    @Autowired private CountryRepository countryRepository;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void findAllCountries() {
        var c1 = utilLocation.savedCountryProj();
        assertThat(service.findAllCountries(), hasItems(c1));
        var c2 = utilLocation.savedCountryProj();
        assertThat(service.findAllCountries(), hasItems(c1, c2));
    }

    @Test
    public void findOrSaveCountry_nullCountry() {
        assertThrows(
                NullPointerException.class,
                () -> service.findOrSaveCountry(null, 1));
    }

    @Test
    public void findOrSaveCountry_saved() {
        var principalId = utilUser.savedAdmin().getId();
        var country = new CountryDto();
        country.setName(genUUID());
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isEmpty());
        service.findOrSaveCountry(country, principalId);
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isPresent());
    }

    @Test
    public void findOrSaveCountry_found() {
        var principalId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        assertTrue(countryRepository.findById(country.getId()).isPresent());
        assertEquals(country, service.findOrSaveCountry(country, principalId));
        assertEquals(country, service.findOrSaveCountry(country, principalId));
    }

    @Test
    public void saveLocation_nullLocation() {
        assertThrows(
                NullPointerException.class,
                () -> service.saveLocation(null, 1));
    }

    @Test
    public void saveLocation() {
        var principalId = utilUser.savedAdmin().getId();
        var location = new LocationDto();
        location.setCountry(utilLocation.savedCountryDto());
        location.setAddress(genUUID());
        assertThat(
                locationRepository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()),
                not(hasItems(location)));
        service.saveLocation(location, principalId);
        assertThat(
                locationRepository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()),
                hasItems(location));
    }

    @Test
    public void createRegion_nullRegion() {
        assertThrows(
                NullPointerException.class,
                () -> service.createRegion(null, 1));
    }

    @Test
    public void createRegion_regionAlreadyExists() {
        var regionDto = utilLocation.genRegionDto();
        service.createRegion(regionDto, 1);
        assertThrows(
                LocationException.class,
                () -> service.createRegion(regionDto, 1));
    }

    @Test
    public void createRegion() {
        var principalId = utilUser.savedAdmin().getId();
        var regionDto = service.createRegion(utilLocation.genRegionDto(), principalId);
        var newlyCreatedRegion = regionRepository
                .findByNameIgnoreCase(regionDto.getName())
                .orElse(null);

        assertNotNull(newlyCreatedRegion);
        assertEquals(regionDto.getId(), newlyCreatedRegion.getId());
        assertEquals(regionDto.getName(), newlyCreatedRegion.getName());
        assertEquals(principalId, newlyCreatedRegion.getCreatorId());
        assertEquals(0, regionDto.getNumberOfCountries().intValue());
        assertEquals(0, regionDto.getNumberOfEvaluators().intValue());
    }

    @Test
    public void createCountry_nullCountry() {
        assertThrows(
                NullPointerException.class,
                () -> service.createCountry(null, 1));
    }

    @Test
    public void createCountry_countryAlreadyExists() {
        var countryDto = utilLocation.genCountryDtoWithRegionDto(utilLocation.savedRegionDto());
        service.createCountry(countryDto, 1);
        assertThrows(
                LocationException.class,
                () -> service.createCountry(countryDto, 1));
    }

    @Test
    public void createCountry_regionNotFound() {
        var countryDto = utilLocation.genCountryDtoWithRegionDto(utilLocation.genRegionDto());
        assertThrows(
                LocationException.class,
                () -> service.createCountry(countryDto, 1));
    }

    @Test
    public void createCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var countryDto = utilLocation.genCountryDtoWithRegionDto(utilLocation.savedRegionDto());
        countryDto = service.createCountry(countryDto, principalId);
        var newlyCreatedCountry = countryRepository
                .findByNameIgnoreCase(countryDto.getName())
                .orElse(null);

        assertNotNull(newlyCreatedCountry);
        assertEquals(countryDto.getId(), newlyCreatedCountry.getId());
        assertEquals(countryDto.getName(), newlyCreatedCountry.getName());
        assertEquals(countryDto.getRegion().getName(), newlyCreatedCountry.getRegion().getName());
        assertEquals(principalId, newlyCreatedCountry.getCreatorId());
    }

    @Test
    public void createCountryEvaluationDetails_nullEvaluationDetails() {
        var countryId = utilLocation.savedCountry().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.createCountryEvaluationDetails(null, countryId, 1));
    }

    @Test
    public void createCountryEvaluationDetails_countryNotFound() {
        var evaluationDetailsDto = utilLocation.genCountryEvaluationDetailsDto();
        assertThrows(
                LocationException.class,
                () -> service.createCountryEvaluationDetails(evaluationDetailsDto, -1, 1));
        ;
    }

    @Test
    public void createCountryEvaluationDetails() {
        var adminId = utilUser.savedAdmin().getId();
        var countryDto = utilLocation.genCountryDtoWithRegionDto(utilLocation.savedRegionDto());
        countryDto = service.createCountry(countryDto, adminId);
        var evaluationDetailsDto = utilLocation.genCountryEvaluationDetailsDto();
        evaluationDetailsDto = service.createCountryEvaluationDetails(evaluationDetailsDto, countryDto.getId(), adminId);

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
