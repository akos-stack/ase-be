package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;

import java.util.List;

public interface ILocationService {

    List<CountryTotalOfEvaluatorsProj> findAllCountries();

    List<CityDto> findAllCities();

    CountryDto findOrSaveCountry(CountryDto countryDto, long principalId);

    CityDto findOrSaveCity(CityDto cityDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

    RegionDto createRegion(RegionDto dto, long principalId);

    RegionDto deleteRegion(int regionId, long principalId);

    CountryDto createCountry(CountryDto dto, long principalId);

    CountryEvaluationDetailsDto createCountryEvaluationDetails(CountryEvaluationDetailsDto dto, int countryId, long principalId);

}
