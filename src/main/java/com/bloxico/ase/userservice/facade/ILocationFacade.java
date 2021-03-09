package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchRegionsResponse findAllRegions();

    SaveRegionResponse saveRegion(SaveRegionRequest request);

    void deleteRegion(Long regionId);

    SearchCountriesResponse findAllCountries();

    SaveCountryResponse saveCountry(SaveCountryRequest request);

    UpdateCountryResponse updateCountry(UpdateCountryRequest request);

}
