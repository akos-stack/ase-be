package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;

public interface ILocationService {

    RegionDto findRegionById(int id);

    RegionDto findRegionByName(String region);

    CountryDto findCountryByName(String country);

    RegionDto saveRegion(RegionDto regionDto, long principalId);

    RegionDto deleteRegion(RegionDto regionDto);

    CountryDto saveCountry(CountryDto countryDto, long principalId);

    CountryDto updateCountry(CountryDto countryDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

    int countCountriesByRegionId(int regionId);

}
