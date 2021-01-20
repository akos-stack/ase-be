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
import static org.junit.Assert.assertEquals;

public class LocationServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationServiceImpl service;

    @Test
    public void findAllCountries() {
        assertEquals(
                List.of(),
                service.findAllCountries());
        var country1 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1),
                service.findAllCountries());
        var country2 = mockUtil.savedCountryDto();
        assertEquals(
                List.of(country1, country2),
                service.findAllCountries());
    }

    @Test
    public void findAllCities() {
        assertEquals(
                List.of(),
                service.findAllCities());
        var city1 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1),
                service.findAllCities());
        var city2 = mockUtil.savedCityDto();
        assertEquals(
                List.of(city1, city2),
                service.findAllCities());
    }

    @Test(expected = NullPointerException.class)
    public void findOrSaveCountry_nullCountry() {
        service.findOrSaveCountry(null, 1);
    }

    @Test
    public void findOrSaveCountry_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = new CountryDto();
        country.setName(uuid());
        assertEquals(List.of(), service.findAllCountries());
        service.findOrSaveCountry(country, principalId);
        assertEquals(List.of(country), service.findAllCountries());
    }

    @Test
    public void findOrSaveCountry_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var country = mockUtil.savedCountryDto();
        assertEquals(List.of(country), service.findAllCountries());
        assertEquals(country, service.findOrSaveCountry(country, principalId));
        assertEquals(List.of(country), service.findAllCountries());
    }

    @Test(expected = NullPointerException.class)
    public void findOrSaveCity_nullCity() {
        service.findOrSaveCity(null, 1);
    }

    @Test
    public void findOrSaveCity_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = new CityDto();
        city.setCountry(mockUtil.savedCountryDto());
        city.setName(uuid());
        assertEquals(List.of(), service.findAllCities());
        service.findOrSaveCity(city, principalId);
        assertEquals(List.of(city), service.findAllCities());
    }

    @Test
    public void findOrSaveCity_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var city = mockUtil.savedCityDto();
        assertEquals(List.of(city), service.findAllCities());
        assertEquals(city, service.findOrSaveCity(city, principalId));
        assertEquals(List.of(city), service.findAllCities());
    }

    @Test(expected = NullPointerException.class)
    public void saveLocation_nullLocation() {
        service.saveLocation(null, 1);
    }

    @Test
    public void saveLocation() {
        var principalId = mockUtil.savedAdmin().getId();
        var location = new LocationDto();
        location.setCity(mockUtil.savedCityDto());
        location.setAddress(uuid());
        assertEquals(List.of(), repository.findAll());
        service.saveLocation(location, principalId);
        assertEquals(
                List.of(location),
                repository
                        .findAll()
                        .stream()
                        .map(MAPPER::toDto)
                        .collect(toList()));
    }

}
