package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchCountriesResponse findAllCountries();

    SearchCitiesResponse findAllCities();

    RegionDataResponse createRegion(CreateRegionRequest request, long id);

    CountryDataResponse createCountry(CreateCountryRequest request, long principalId);

}
