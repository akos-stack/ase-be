package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchCountriesResponse findAllCountries();

    SearchCitiesResponse findAllCities();

    RegionDataResponse createRegion(CreateRegionRequest request, long principalId);

    void deleteRegion(int regionId, long principalId);

    CountryDataResponse createCountry(CreateCountryRequest request, long principalId);

}
