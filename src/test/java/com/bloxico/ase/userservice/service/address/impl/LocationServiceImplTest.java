package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private LocationRepository locationRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private RegionRepository regionRepository;
    @Autowired private LocationServiceImpl service;

    @Test
    public void saveRegion_nullRegion() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.saveRegion(null, principalId));
    }

    @Test
    public void saveRegion_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var region = utilLocation.savedRegionDto();
        assertTrue(regionRepository.findById(region.getId()).isPresent());
        assertThrows(
                LocationException.class,
                () -> service.saveRegion(region, principalId));
    }

    @Test
    public void saveRegion() {
        var principalId = utilUser.savedAdmin().getId();
        var region = new RegionDto();
        region.setName(genUUID());
        assertTrue(regionRepository.findByNameIgnoreCase(region.getName()).isEmpty());
        service.saveRegion(region, principalId);
        assertTrue(regionRepository.findByNameIgnoreCase(region.getName()).isPresent());
    }

    @Test
    public void saveCountry_nullCountry() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.saveCountry(null, principalId));
    }

    @Test
    public void saveCountry_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        assertTrue(countryRepository.findById(country.getId()).isPresent());
        assertThrows(
                LocationException.class,
                () -> service.saveCountry(country, principalId));
    }

    @Test
    public void saveCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var country = new CountryDto();
        country.setName(genUUID());
        country.setRegion(utilLocation.savedRegionDto());
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isEmpty());
        service.saveCountry(country, principalId);
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isPresent());
    }

    @Test
    public void saveLocation_nullLocation() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> service.saveLocation(null, principalId));
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

}
