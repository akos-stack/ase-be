package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchRegionsResponse findAllRegions();

    SaveRegionResponse saveRegion(SaveRegionRequest request, long principalId);

    void deleteRegion(int regionId);

    SearchCountriesResponse findAllCountries();

    SaveCountryResponse saveCountry(SaveCountryRequest request, long principalId);

    UpdateCountryResponse updateCountry(UpdateCountryRequest request, long principalId);

}
