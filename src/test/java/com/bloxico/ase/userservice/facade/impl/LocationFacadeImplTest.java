package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationFacadeImpl facade;

    @Test
    public void findAllCountries() {
        var c1 = mockUtil.savedCountryDto();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1));
        var c2 = mockUtil.savedCountryDto();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1, c2));
    }

    @Test
    public void findAllCities() {
        var c1 = mockUtil.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1));
        var c2 = mockUtil.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1, c2));
    }

}
