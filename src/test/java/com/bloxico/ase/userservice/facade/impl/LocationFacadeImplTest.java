package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
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
    public void allCountries() {
        assertEquals(
                List.of(),
                facade.allCountries());
        var country1 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1),
                facade.allCountries());
        var country2 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1, country2),
                facade.allCountries());
    }

    @Test
    public void allCities() {
        assertEquals(
                List.of(),
                facade.allCities());
        var city1 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1),
                facade.allCities());
        var city2 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1, city2),
                facade.allCities());
    }

}
