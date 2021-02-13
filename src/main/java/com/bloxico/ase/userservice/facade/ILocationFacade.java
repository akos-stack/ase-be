package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchCountriesResponse findAllCountries(SearchCountriesRequest request);

    SearchCitiesResponse findAllCities();

    SearchRegionsResponse findAllRegions(SearchRegionsRequest request);

    RegionDataResponse createRegion(CreateRegionRequest request, long principalId);

    void deleteRegion(int regionId, long principalId);

    CountryDataResponse createCountry(CreateCountryRequest request, long principalId);

    void editCountry(EditCountryRequest request, int countryId, long principalId);

}
