package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genUUID;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private LocationFacadeImpl facade;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void findAllRegions() {
        var r1 = utilLocation.savedRegionDto();
        assertThat(facade.findAllRegions().getRegions(), hasItems(r1));
        var r2 = utilLocation.savedRegionDto();
        assertThat(facade.findAllRegions().getRegions(), hasItems(r1, r2));
    }

    @Test
    public void saveRegion_nullRequest() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> facade.saveRegion(null, principalId));
    }

    @Test
    public void saveRegion_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var region = utilLocation.savedRegion();
        var request = new SaveRegionRequest(region.getName());
        assertThrows(
                LocationException.class,
                () -> facade.saveRegion(request, principalId));
    }

    @Test
    public void saveRegion() {
        var principalId = utilUser.savedAdmin().getId();
        var request = new SaveRegionRequest(genUUID());
        var region = facade.saveRegion(request, principalId).getRegion();
        assertNotNull(region.getId());
        assertEquals(request.getRegion(), region.getName());
    }

    @Test
    public void deleteRegion_regionNotFound() {
        assertThrows(
                LocationException.class,
                () -> facade.deleteRegion(-1));
    }

    @Test
    public void deleteRegion_regionHasCountries() {
        var region = utilLocation.savedRegion();
        utilLocation.savedCountryWithRegion(region);
        assertThrows(
                LocationException.class,
                () -> facade.deleteRegion(region.getId())
        );
    }

    @Test
    public void deleteRegion() {
        var region = utilLocation.savedRegion();
        facade.deleteRegion(region.getId());
        assertTrue(regionRepository.findById(region.getId()).isEmpty());
    }

    @Test
    public void findAllCountries() {
        var c1 = utilLocation.savedCountryDto();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1));
        var c2 = utilLocation.savedCountryDto();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1, c2));
    }

    @Test
    public void saveCountry_requestIsNull() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> facade.saveCountry(null, principalId));
    }

    @Test
    public void saveCountry_regionNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var request = new SaveCountryRequest(genUUID(), Set.of(genUUID()));
        assertThrows(
                LocationException.class,
                () -> facade.saveCountry(request, principalId));
    }

    @Test
    public void saveCountry_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var regionName = utilLocation.savedRegion().getName();
        var request = new SaveCountryRequest(genUUID(), Set.of(regionName));
        facade.saveCountry(request, principalId);
        assertThrows(
                LocationException.class,
                () -> facade.saveCountry(request, principalId));
    }

    @Test
    public void saveCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var region = utilLocation.savedRegionDto();
        var request = new SaveCountryRequest(genUUID(), Set.of(region.getName()));
        var country = facade.saveCountry(request, principalId).getCountry();
        assertNotNull(country.getId());
        assertNotNull(country.getRegions());
        assertThat(country.getRegions(), hasItems(region));
        assertEquals(request.getCountry(), country.getName());
    }

    @Test
    public void updateCountry_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> facade.updateCountry(null,
                        utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_countryAlreadyExists() {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithRegion(region);
        var request = new UpdateCountryRequest(utilLocation.savedCountry().getId(), country.getName(), Set.of(region.getName()));
        assertThrows(
                LocationException.class,
                () -> facade.updateCountry(request,
                        utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_regionNotFound() {
        var request = new UpdateCountryRequest(utilLocation.savedCountry().getId(), genUUID(), Set.of(genUUID()));
        assertThrows(
                LocationException.class,
                () -> facade.updateCountry(request,
                        utilUser.savedAdmin().getId()));
    }

    @Test
    public void updateCountry_updateName() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var newCountryName = genUUID();
        var request = new UpdateCountryRequest(
                country.getId(),
                newCountryName,
                country
                        .getRegions()
                        .stream()
                        .map(RegionDto::getName)
                        .collect(toSet()));
        var updatedCountry = facade.updateCountry(request, adminId).getCountry();
        assertNotEquals(country.getName(), updatedCountry.getName());
        assertEquals(country.getRegions(), updatedCountry.getRegions());
    }

    @Test
    public void updateCountry_updateRegions() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var region = utilLocation.savedRegionDto();
        var newRegionName = region.getName();
        var request = new UpdateCountryRequest(country.getId(), country.getName(), Set.of(newRegionName));
        var updatedCountry = facade.updateCountry(request, adminId).getCountry();
        assertEquals(country.getName(), updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(region));
        for (var oldRegion : country.getRegions())
            assertThat(updatedCountry.getRegions(), not(hasItems(oldRegion)));
    }

    @Test
    public void updateCountry_updateNameAndRegions() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountryDto();
        var region = utilLocation.savedRegionDto();
        var newCountryName = genUUID();
        var newRegionName = region.getName();
        var request = new UpdateCountryRequest(country.getId(), newCountryName, Set.of(newRegionName));
        var updatedCountry = facade.updateCountry(request, adminId).getCountry();
        assertEquals(newCountryName, updatedCountry.getName());
        assertNotEquals(country.getName(), updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(region));
        for (var oldRegion : country.getRegions())
            assertThat(updatedCountry.getRegions(), not(hasItems(oldRegion)));
    }

}
