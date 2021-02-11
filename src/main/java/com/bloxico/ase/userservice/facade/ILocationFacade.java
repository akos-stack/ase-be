package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchCountriesResponse findAllCountries();

    RegionDataResponse createRegion(CreateRegionRequest request, long principalId);

    CountryDataResponse createCountry(CreateCountryRequest request, long principalId);

}
