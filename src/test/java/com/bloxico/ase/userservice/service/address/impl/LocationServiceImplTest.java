package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    public void findAllCities() {
        var c1 = utilLocation.savedCityDto();
        assertThat(service.findAllCities(), hasItems(c1));
        var c2 = utilLocation.savedCityDto();
        assertThat(service.findAllCities(), hasItems(c1, c2));
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
    public void findOrSaveCity_nullCity() {
        assertThrows(
                NullPointerException.class,
                () -> service.findOrSaveCity(null, 1));
    }

    @Test
    public void findOrSaveCity_saved() {
        var principalId = utilUser.savedAdmin().getId();
        var city = new CityDto();
        city.setCountry(utilLocation.savedCountryDto());
        city.setName(genUUID());
        assertThat(service.findAllCities(), not(hasItems(city)));
        service.findOrSaveCity(city, principalId);
        assertThat(service.findAllCities(), hasItems(city));
    }

    @Test
    public void findOrSaveCity_found() {
        var principalId = utilUser.savedAdmin().getId();
        var city = utilLocation.savedCityDto();
        assertThat(service.findAllCities(), hasItems(city));
        assertEquals(city, service.findOrSaveCity(city, principalId));
        assertEquals(
                List.of(city),
                service.findAllCities()
                        .stream()
                        .filter(city::equals)
                        .collect(toList()));
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
    public void deleteRegion_regionNotFound() {
        assertThrows(
                LocationException.class,
                () -> service.deleteRegion(-1, utilUser.savedAdmin().getId()));
    }

    @Test
    public void deleteRegion_regionHasCountries() {
        var adminId = utilUser.savedAdmin().getId();
        var regionDto = utilLocation.savedRegionDto();
        var countryDto = utilLocation.genCountryDtoWithRegionDto(regionDto);
        service.createCountry(countryDto, adminId);
        assertThrows(
                LocationException.class,
                () -> service.deleteRegion(regionDto.getId(), adminId));
    }

    @Test
    public void deleteRegion() {
        var id = utilLocation.savedRegion().getId();
        assertTrue(regionRepository.findById(id).isPresent());
        service.deleteRegion(id, utilUser.savedAdmin().getId());
        assertTrue(regionRepository.findById(id).isEmpty());
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

    @Test
    public void editCountry_nullCountry() {
        assertThrows(
                NullPointerException.class,
                () -> service.editCountry(null, 1, 1));
    }

    @Test
    public void editCountry_countryAlreadyExists() {
        var country1 = utilLocation.savedCountryDto();
        var country2 = utilLocation.savedCountryDto();
        country1.setName(country2.getName());
        assertThrows(
                LocationException.class,
                () -> service.editCountry(country1, country1.getId(), 1));
    }

    @Test
    public void editCountry_regionNotFound() {
        var country = utilLocation.savedCountryDto();
        country.setRegion(utilLocation.genRegionDto());
        assertThrows(
                LocationException.class,
                () -> service.editCountry(country, country.getId(), 1));
    }

    @Test
    public void editCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountryDto().getId();
        var regionDto = utilLocation.savedRegionDto();

        var dto = utilLocation.genCountryDtoWithRegionDto(regionDto);
        service.editCountry(dto, countryId, principalId);

        var editedCountry = countryRepository
                .findById(countryId)
                .orElse(null);

        assertNotNull(editedCountry);
        assertEquals(dto.getName(), editedCountry.getName());
        assertEquals(regionDto.getName(), editedCountry.getRegion().getName());
        assertEquals(principalId, editedCountry.getUpdaterId());
    }

    @Test
    public void editCountryEvaluationDetails_nullEvaluationDetails() {
        var countryId = utilLocation.savedCountry().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.editCountryEvaluationDetails(null, countryId, 1));
    }

    @Test
    public void editCountryEvaluationDetails_countryNotFound() {
        var evaluationDetailsDto = utilLocation.genCountryEvaluationDetailsDto();
        assertThrows(
                LocationException.class,
                () -> service.editCountryEvaluationDetails(evaluationDetailsDto, -1, 1));
    }

    @Test
    public void editCountryEvaluationDetails() {
        var adminId = utilUser.savedAdmin().getId();
        var countryDto = utilLocation.savedCountryDto();
        var evaluationDetailsDto = utilLocation.genCountryEvaluationDetailsDto(5, 55);
        evaluationDetailsDto = service.editCountryEvaluationDetails(evaluationDetailsDto, countryDto.getId(), adminId);

        var editedEvaluationDetails = countryEvaluationDetailsRepository
                .findById(evaluationDetailsDto.getId())
                .orElse(null);

        assertNotNull(editedEvaluationDetails);
        assertEquals(countryDto.getId(), editedEvaluationDetails.getCountry().getId());
        assertEquals(evaluationDetailsDto.getPricePerEvaluation(), editedEvaluationDetails.getPricePerEvaluation());
        assertEquals(evaluationDetailsDto.getAvailabilityPercentage(), editedEvaluationDetails.getAvailabilityPercentage());
        assertEquals(adminId, editedEvaluationDetails.getUpdaterId());
    }

}
