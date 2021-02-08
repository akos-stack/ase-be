package com.bloxico.ase.userservice.service.address.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationServiceImpl service;

    @Test
    public void findAllCountries() {
        var c1 = mockUtil.savedCountryDto();
        assertThat(service.findAllCountries(), hasItems(c1));
        var c2 = mockUtil.savedCountryDto();
        assertThat(service.findAllCountries(), hasItems(c1, c2));
    }

    @Test
    public void findAllCities() {
        var c1 = mockUtil.savedCityDto();
        assertThat(service.findAllCities(), hasItems(c1));
        var c2 = mockUtil.savedCityDto();
        assertThat(service.findAllCities(), hasItems(c1, c2));
    }

    @Test
    public void findOrSaveCountry_nullCountry() {
        assertThrows(
                NullPointerException.class,
                () -> service.findOrSaveCountry(null, 1));
    }

    @Test
    public void findOrSaveCountry_saved() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = new CountryDto();
        country.setName(uuid());
        assertThat(service.findAllCountries(), not(hasItems(country)));
        service.findOrSaveCountry(country, principalId);
        assertThat(service.findAllCountries(), hasItems(country));
    }

    @Test
    public void findOrSaveCountry_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = mockUtil.savedCountryDto();
        assertThat(service.findAllCountries(), hasItems(country));
        assertEquals(country, service.findOrSaveCountry(country, principalId));
        assertEquals(
                List.of(country),
                service.findAllCountries()
                        .stream()
                        .filter(country::equals)
                        .collect(toList()));
    }

    @Test
    public void findOrSaveCity_nullCity() {
        assertThrows(
                NullPointerException.class,
                () -> service.findOrSaveCity(null, 1));
    }

    @Test
    public void findOrSaveCity_saved() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = new CityDto();
        city.setCountry(mockUtil.savedCountryDto());
        city.setName(uuid());
        assertThat(service.findAllCities(), not(hasItems(city)));
        service.findOrSaveCity(city, principalId);
        assertThat(service.findAllCities(), hasItems(city));
    }

    @Test
    public void findOrSaveCity_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = mockUtil.savedCityDto();
        assertThat(service.findAllCities(), hasItems(city));
        assertEquals(city, service.findOrSaveCity(city, principalId));
        assertEquals(
                List.of(city),
                service.findAllCities()
                        .stream()
                        .filter(city::equals)
                        .collect(toList()));
    }

    @Test
    public void saveLocation_nullLocation() {
        assertThrows(
                NullPointerException.class,
                () -> service.saveLocation(null, 1));
    }

    @Test
    public void saveLocation() {
        var principalId = mockUtil.savedAdmin().getId();
        var location = new LocationDto();
        location.setCity(mockUtil.savedCityDto());
        location.setAddress(uuid());
        assertThat(
                repository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()),
                not(hasItems(location)));
        service.saveLocation(location, principalId);
        assertThat(
                repository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()),
                hasItems(location));
    }

}
