package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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
    @Autowired private CountryRepository countryRepository;
    @Autowired private RegionRepository regionRepository;
    @Autowired private LocationServiceImpl service;

    @Test
    public void findRegionById_regionNotFound() {
        assertThrows(
                LocationException.class,
                () -> service.findRegionById(-1));
    }

    @Test
    public void findRegionById() {
        var regionDto = utilLocation.savedRegionDto();
        var foundRegion = service.findRegionById(regionDto.getId());
        assertEquals(foundRegion, regionDto);
    }

    @Test
    public void findRegionByName_regionNotFound() {
        assertThrows(
                LocationException.class,
                () -> service.findRegionByName(genUUID()));
    }

    @Test
    public void findRegionByName_nullName() {
        assertThrows(
                LocationException.class,
                () -> service.findRegionByName(null));
    }

    @Test
    public void findRegionByName_emptyName() {
        assertThrows(
                LocationException.class,
                () -> service.findRegionByName(""));
    }

    @Test
    public void findRegionByName() {
        var regionDto = utilLocation.savedRegionDto();
        var foundRegion = service.findRegionByName(regionDto.getName());
        assertEquals(regionDto, foundRegion);
    }

    @Test
    public void findAllRegionsWithNames_nullNames() {
        assertThrows(
                NullPointerException.class,
                () -> service.findAllRegionsWithNames(null));
    }

    @Test
    public void findAllRegionsWithNames_emptyNames() {
        assertEquals(List.of(), service.findAllRegionsWithNames(List.of()));
    }

    @Test
    public void findAllRegionsWithNames_nothingFound() {
        assertEquals(List.of(), service.findAllRegionsWithNames(List.of(genUUID())));
    }

    @Test
    public void findAllRegionsWithNames() {
        var r1 = utilLocation.savedRegionDto();
        var r2 = utilLocation.savedRegionDto();
        var r3 = utilLocation.savedRegionDto();
        var foundRegions = service.findAllRegionsWithNames(
                List.of(r1.getName(), r2.getName(), r3.getName()));
        assertThat(foundRegions, hasItems(r1, r2, r3));
    }

    @Test
    public void findCountryById_countryNotFound() {
        assertThrows(
                LocationException.class,
                () -> service.findCountryById(-1));
    }

    @Test
    public void findCountryById() {
        var countryDto = utilLocation.savedCountryDto();
        var foundCountry = service.findCountryById(countryDto.getId());
        assertEquals(countryDto, foundCountry);
    }

    @Test
    public void findCountryByName_countryNotFound() {
        assertThrows(
                LocationException.class,
                () -> service.findCountryByName(genUUID()));
    }

    @Test
    public void findCountryByName() {
        var countryDto = utilLocation.savedCountryDto();
        var foundCountry = service.findCountryByName(countryDto.getName());
        assertEquals(countryDto, foundCountry);
    }

    @Test
    public void findAllRegions() {
        var r1 = utilLocation.savedRegionDto();
        assertThat(service.findAllRegions(), hasItems(r1));
        var r2 = utilLocation.savedRegionDto();
        assertThat(service.findAllRegions(), hasItems(r1, r2));
    }

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
    public void deleteRegion_nullRegion() {
        assertThrows(
                NullPointerException.class,
                () -> service.deleteRegion(null));
    }

    @Test
    public void deleteRegion() {
        var regionDto = utilLocation.savedRegionDto();
        service.deleteRegion(regionDto);
        assertTrue(regionRepository.findById(regionDto.getId()).isEmpty());
    }

    @Test
    public void findAllCountries() {
        var c1 = utilLocation.savedCountryDto();
        assertThat(service.findAllCountries(), hasItems(c1));
        var c2 = utilLocation.savedCountryDto();
        assertThat(service.findAllCountries(), hasItems(c1, c2));
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
        country.setRegions(Set.of(utilLocation.savedRegionDto()));
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isEmpty());
        service.saveCountry(country, principalId);
        assertTrue(countryRepository.findByNameIgnoreCase(country.getName()).isPresent());
    }

    @Test
    public void updateCountry_nullCountry() {
        assertThrows(
                NullPointerException.class,
                () -> service.updateCountry(null, utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_countryNotFound() {
        var countryDto = utilLocation.savedCountryDto();
        countryDto.setId(-1);
        assertThrows(
                LocationException.class,
                () -> service.updateCountry(countryDto, utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_countryAlreadyExists() {
        var countryDto = utilLocation.savedCountryDto();
        countryDto.setName(utilLocation.savedCountry().getName());
        assertThrows(
                LocationException.class,
                () -> service.updateCountry(countryDto, utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_updateName() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var newCountryName = genUUID();
        var dto = utilLocation.genCountryDto(country.getId(), newCountryName, country.getRegions());
        var updatedCountry = service.updateCountry(dto, adminId);
        assertNotEquals(country.getName(), updatedCountry.getName());
        assertEquals(country.getRegions(), updatedCountry.getRegions());
    }

    @Test
    public void updateCountry_updateRegions() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var newRegion = utilLocation.savedRegionDto();
        var dto = utilLocation.genCountryDto(country.getId(), country.getName(), Set.of(newRegion));
        var updatedCountry = service.updateCountry(dto, adminId);
        assertEquals(country.getName(), updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(newRegion));
        for (var oldRegion : country.getRegions())
            assertThat(updatedCountry.getRegions(), not(hasItems(oldRegion)));
    }

    @Test
    public void updateCountry_updateNameAndRegions() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var newRegion = utilLocation.savedRegionDto();
        var newCountryName = genUUID();
        var dto = utilLocation.genCountryDto(country.getId(), newCountryName, Set.of(newRegion));
        var updatedCountry = service.updateCountry(dto, adminId);
        assertEquals(newCountryName, updatedCountry.getName());
        assertNotEquals(country.getName(), updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(newRegion));
        for (var oldRegion : country.getRegions())
            assertThat(updatedCountry.getRegions(), not(hasItems(oldRegion)));
    }

    @Test
    public void updateCountry() {
        var country = utilLocation.savedCountryDto();
        var region = utilLocation.savedRegionDto();
        var newCountryName = genUUID();
        var dto = new CountryDto();
        dto.setId(country.getId());
        dto.setName(newCountryName);
        dto.setRegions(Set.of(region));
        var updatedCountry = service.updateCountry(dto, utilUser.savedAdmin().getId());
        assertEquals(newCountryName, updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(region));
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

    @Test
    public void countCountriesByRegionId_regionNotFound() {
        assertEquals(0, service.countCountriesByRegionId(-1));
    }

    @Test
    public void countCountriesByRegionId() {
        var region = utilLocation.savedRegion();
        assertEquals(0, service.countCountriesByRegionId(region.getId()));
        utilLocation.savedCountryWithRegion(region);
        assertEquals(1, service.countCountriesByRegionId(region.getId()));
    }

}
