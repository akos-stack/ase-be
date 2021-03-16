package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;

import java.util.Collection;
import java.util.List;

public interface ILocationService {

    RegionDto findRegionById(long id);

    RegionDto findRegionByName(String region);

    List<RegionDto> findAllRegionsWithNames(Collection<String> regionNames);

    CountryDto findCountryById(long id);

    CountryDto findCountryByName(String country);

    List<RegionDto> findAllRegions();

    RegionDto saveRegion(RegionDto regionDto);

    RegionDto deleteRegion(RegionDto regionDto);

    List<CountryDto> findAllCountries();

    CountryDto saveCountry(CountryDto countryDto);

    CountryDto updateCountry(CountryDto countryDto);

    LocationDto saveLocation(LocationDto locationDto);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

    LocationDto findLocationById(long id);

    int countCountriesByRegionId(long regionId);

}
