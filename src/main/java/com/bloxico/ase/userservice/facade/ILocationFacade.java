package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.address.*;

public interface ILocationFacade {

    SaveRegionResponse saveRegion(SaveRegionRequest request, long principalId);

    SaveCountryResponse saveCountry(SaveCountryRequest request, long principalId);

}
