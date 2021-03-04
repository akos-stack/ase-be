package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;

import java.util.Collection;
import java.util.List;

public interface ILocationService {

    RegionDto findRegionById(Long id);

    RegionDto findRegionByName(String region);

    List<RegionDto> findAllRegionsWithNames(Collection<String> regionNames);

    CountryDto findCountryByName(String country);

    List<RegionDto> findAllRegions();

    RegionDto saveRegion(RegionDto regionDto);

    RegionDto deleteRegion(RegionDto regionDto);

    List<CountryDto> findAllCountries();

    CountryDto saveCountry(CountryDto countryDto);

    CountryDto updateCountry(CountryDto countryDto);

    LocationDto saveLocation(LocationDto locationDto, Long principalId);

    int countCountriesByRegionId(Long regionId);

}
