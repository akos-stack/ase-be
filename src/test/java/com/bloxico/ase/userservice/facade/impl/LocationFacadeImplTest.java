package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.*;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationFacadeImpl facade;

    @Autowired
    private RegionRepository regionRepository;

    @Test
    public void findAllCountries() {
        assertEquals(
                new SearchCountriesResponse(List.of()),
                facade.findAllCountries());
        var country1 = mockUtil.savedCountryDto();
        assertEquals(
                new SearchCountriesResponse(List.of(country1)),
                facade.findAllCountries());
        var country2 = mockUtil.savedCountryDto();
        assertEquals(
                new SearchCountriesResponse(List.of(country1, country2)),
                facade.findAllCountries());
    }

    @Test
    public void findAllCities() {
        assertEquals(
                new SearchCitiesResponse(List.of()),
                facade.findAllCities());
        var city1 = mockUtil.savedCityDto();
        assertEquals(
                new SearchCitiesResponse(List.of(city1)),
                facade.findAllCities());
        var city2 = mockUtil.savedCityDto();
        assertEquals(
                new SearchCitiesResponse(List.of(city1, city2)),
                facade.findAllCities());
    }

    @Test(expected = NullPointerException.class)
    public void createRegion_nullRequest() {
        facade.createRegion(null, 1);
    }

    @Test(expected = LocationException.class)
    public void createRegion_regionAlreadyExists() {
        var regionName = mockUtil.savedRegion().getName();
        var request = new CreateRegionRequest(regionName);
        facade.createRegion(request, 1);
    }

    @Test
    public void createRegion_regionSuccessfullyCreated() {
        var principalId = mockUtil.savedAdmin().getId();
        var regionName = uuid();
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

}
