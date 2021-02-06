package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.CreateCountryRequest;
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

    @Autowired
    private CountryRepository countryRepository;

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

    @Test(expected = NullPointerException.class)
    public void createCountry_requestIsNull() {
        facade.createCountry(null, 1);
    }

    @Test(expected = LocationException.class)
    public void createCountry_countryAlreadyExists() {
        var region = mockUtil.savedRegion();
        var request = new CreateCountryRequest(uuid(), region.getName(), 10, 40);
        facade.createCountry(request, 1);
        facade.createCountry(request, 1);
    }

    @Test(expected = LocationException.class)
    public void createCountry_regionNotFound() {
        var request = new CreateCountryRequest(uuid(), uuid(), 10, 40);
        facade.createCountry(request, 1);
    }

    @Test
    public void createCountry_countrySuccessfullyCreated() {
        var principalId = mockUtil.savedAdmin().getId();
        var name = uuid();
        var region = mockUtil.savedRegion();

        var request = new CreateCountryRequest(name, region.getName(), 10, 40);
        var response = facade.createCountry(request, principalId);

        assertNotNull(response);
        assertNotNull(response.getCountry());

        var newlyCreatedCountry = countryRepository
                .findByNameIgnoreCase(name)
                .orElse(null);

        assertNotNull(newlyCreatedCountry);
        assertEquals(region.getName(), newlyCreatedCountry.getRegion().getName());
        assertEquals(response.getCountry().getId(), newlyCreatedCountry.getId());
        assertEquals(name, newlyCreatedCountry.getName());
        assertEquals(principalId, newlyCreatedCountry.getCreatorId());
        assertEquals(response.getCountry().getCountryEvaluationDetails().getPricePerEvaluation(), 10);
        assertEquals(response.getCountry().getCountryEvaluationDetails().getAvailabilityPercentage(), 40);
        assertEquals(response.getCountry().getCountryEvaluationDetails().getTotalOfEvaluators(), 0);
    }

}
