package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.SaveCountryRequest;
import com.bloxico.ase.userservice.web.model.address.SaveRegionRequest;
import com.bloxico.ase.userservice.web.model.address.UpdateCountryRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.hasItems;
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
                () -> facade.deleteRegion(-1)
        );
    }

    @Test
    public void deleteRegion_regionHasCountries() {
        var country = utilLocation.savedCountry();
        assertThrows(
                LocationException.class,
                () -> facade.deleteRegion(-country.getRegion().getId())
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
    public void saveCountry_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var regionName = utilLocation.savedRegion().getName();
        var request = new SaveCountryRequest(genUUID(), regionName);
        facade.saveCountry(request, principalId);
        assertThrows(
                LocationException.class,
                () -> facade.saveCountry(request, principalId));
    }

    @Test
    public void saveCountry_regionNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var request = new SaveCountryRequest(genUUID(), genUUID());
        assertThrows(
                LocationException.class,
                () -> facade.saveCountry(request, principalId));
    }

    @Test
    public void saveCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var region = utilLocation.savedRegionDto();
        var request = new SaveCountryRequest(genUUID(), region.getName());
        var country = facade.saveCountry(request, principalId).getCountry();
        assertNotNull(country.getId());
        assertNotNull(country.getRegion().getId());
        assertEquals(region, country.getRegion());
        assertEquals(request.getCountry(), country.getName());
    }

    @Test
    public void updateCountry_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> facade.updateCountry(null,
                        utilLocation.savedCountry().getId(),
                        utilUser.savedAdmin().getId())
        );
    }

    @Test
    public void updateCountry_countryAlreadyExists() {
        var country = utilLocation.savedCountry();
        var request = new UpdateCountryRequest(country.getName(), country.getRegion().getName());
        assertThrows(
                LocationException.class,
                () -> facade.updateCountry(request,
                        utilLocation.savedCountry().getId(),
                        utilUser.savedAdmin().getId())
        );
    }

    @Test
    public void updateCountry_regionNotFound() {
        var request = new UpdateCountryRequest(genUUID(), genUUID());
        assertThrows(
                LocationException.class,
                () -> facade.updateCountry(request,
                        utilLocation.savedCountry().getId(),
                        utilUser.savedAdmin().getId())
        );
    }

    @Test
    public void updateCountry() {
        var adminId = utilUser.savedAdmin().getId();
        var country = utilLocation.savedCountry();
        var region = utilLocation.savedRegion();
        var newCountryName = genUUID();
        var newRegionName = region.getName();
        var request = new UpdateCountryRequest(newCountryName, newRegionName);
        var updatedCountry = facade.updateCountry(request, country.getId(), adminId).getCountry();
        assertEquals(newCountryName, updatedCountry.getName());
        assertEquals(newRegionName, updatedCountry.getRegion().getName());
    }

}
