package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.CreateCountryRequest;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
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
    @Autowired private CountryRepository countryRepository;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void findAllCountries() {
        var c1 = utilLocation.savedCountryProj();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1));
        var c2 = utilLocation.savedCountryProj();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1, c2));
    }

    @Test
    public void findAllCities() {
        var c1 = utilLocation.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1));
        var c2 = utilLocation.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1, c2));
    }

    @Test
    public void createRegion_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> facade.createRegion(null, 1));
    }

    @Test
    public void createRegion_regionAlreadyExists() {
        var regionName = utilLocation.savedRegion().getName();
        var request = new CreateRegionRequest(regionName);
        assertThrows(
                LocationException.class,
                () -> facade.createRegion(request, 1));
    }

    @Test
    public void createRegion() {
        var principalId = utilUser.savedAdmin().getId();
        var regionName = genUUID();
        var request = new CreateRegionRequest(regionName);
        var response = facade.createRegion(request, principalId);

        assertNotNull(response);
        assertNotNull(response.getRegion());

        var newlyCreatedRegion = regionRepository
                .findByNameIgnoreCase(response.getRegion().getName())
                .orElse(null);

        assertNotNull(newlyCreatedRegion);
        assertEquals(regionName, newlyCreatedRegion.getName());
        assertEquals(principalId, newlyCreatedRegion.getCreatorId());
        assertEquals(response.getRegion().getId(), newlyCreatedRegion.getId());
        assertEquals(0, response.getRegion().getNumberOfCountries().intValue());
        assertEquals(0, response.getRegion().getNumberOfEvaluators().intValue());
    }

    @Test
    public void createCountry_requestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> facade.createCountry(null, 1));
    }

    @Test
    public void createCountry_countryAlreadyExists() {
        var regionName = utilLocation.savedRegion().getName();
        var request = new CreateCountryRequest(genUUID(), regionName, 10, 40);
        facade.createCountry(request, 1);
        assertThrows(
                LocationException.class,
                () -> facade.createCountry(request, 1));
    }

    @Test
    public void createCountry_regionNotFound() {
        var request = new CreateCountryRequest(genUUID(), genUUID(), 10, 40);
        assertThrows(
                LocationException.class,
                () -> facade.createCountry(request, 1));
    }

    @Test
    public void createCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var name = genUUID();
        var region = utilLocation.savedRegion();

        var request = new CreateCountryRequest(name, region.getName(), 10, 40);
        var response = facade.createCountry(request, principalId);

        assertNotNull(response);
        assertNotNull(response.getCountry());
        var countryResponse = response.getCountry();

        var newlyCreatedCountry = countryRepository
                .findByNameIgnoreCase(name)
                .orElse(null);

        assertNotNull(newlyCreatedCountry);
        assertEquals(countryResponse.getId(), newlyCreatedCountry.getId());
        assertEquals(countryResponse.getName(), newlyCreatedCountry.getName());
        assertEquals(countryResponse.getRegion().getName(), newlyCreatedCountry.getRegion().getName());
        assertEquals(principalId, newlyCreatedCountry.getCreatorId());
        assertEquals(10, countryResponse.getCountryEvaluationDetails().getPricePerEvaluation());
        assertEquals(40, countryResponse.getCountryEvaluationDetails().getAvailabilityPercentage());
        assertEquals(0, countryResponse.getCountryEvaluationDetails().getTotalOfEvaluators());
    }

}
