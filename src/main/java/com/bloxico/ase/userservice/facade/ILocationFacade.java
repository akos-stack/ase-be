package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SearchRegionsResponse findAllRegions();

    SaveRegionResponse saveRegion(SaveRegionRequest request, long principalId);

    SaveCountryResponse saveCountry(SaveCountryRequest request, long principalId);

    UpdateCountryResponse updateCountry(UpdateCountryRequest request, int countryId, long principalId);

    void deleteRegion(int regionId);

}
