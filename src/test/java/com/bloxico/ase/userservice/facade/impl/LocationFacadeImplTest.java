package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationFacadeImpl facade;

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

}
