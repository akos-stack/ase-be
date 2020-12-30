package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;

import java.util.List;

public interface ILocationService {

    List<CountryDto> allCountries();

    List<CityDto> allCities();

    CountryDto findOrSaveCountry(CountryDto countryDto, long principalId);

    CityDto findOrSaveCity(CityDto cityDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

}