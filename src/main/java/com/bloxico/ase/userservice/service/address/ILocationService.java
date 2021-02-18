package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;

public interface ILocationService {

    RegionDto findRegionByName(String region);

    CountryDto findCountryByName(String region);

    RegionDto saveRegion(RegionDto regionDto, long principalId);

    CountryDto saveCountry(CountryDto countryDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

}
