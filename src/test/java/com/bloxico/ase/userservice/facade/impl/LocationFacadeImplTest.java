package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.web.model.address.SaveCountryRequest;
import com.bloxico.ase.userservice.web.model.address.SaveRegionRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private LocationFacadeImpl facade;

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

}
