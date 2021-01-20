package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;

import java.util.List;

public interface ILocationFacade {

    List<CountryDto> findAllCountries();

    List<CityDto> findAllCities();

}
