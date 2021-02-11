package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;

import java.util.List;

public interface ILocationService {

    List<CountryTotalOfEvaluatorsProj> findAllCountries();

    CountryDto findOrSaveCountry(CountryDto countryDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

    RegionDto createRegion(RegionDto dto, long principalId);

    CountryDto createCountry(CountryDto dto, long principalId);

    CountryEvaluationDetailsDto createCountryEvaluationDetails(CountryEvaluationDetailsDto dto, int countryId, long principalId);

}
