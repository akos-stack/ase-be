package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;

public interface ILocationFacade {

    SearchCountriesResponse findAllCountries();

    SearchCitiesResponse findAllCities();

}
