package com.bloxico.ase.userservice.service.address;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.bloxico.ase.userservice.projection.RegionDetailsProj;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesRequest;
import com.bloxico.ase.userservice.web.model.address.SearchRegionsRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ILocationService {

    Page<CountryTotalOfEvaluatorsProj> findAllCountries(SearchCountriesRequest request);

    List<CityDto> findAllCities();

    CountryDto findOrSaveCountry(CountryDto countryDto, long principalId);

    CityDto findOrSaveCity(CityDto cityDto, long principalId);

    LocationDto saveLocation(LocationDto locationDto, long principalId);

    Page<RegionDetailsProj> findAllRegions(SearchRegionsRequest request);

    RegionDto createRegion(RegionDto dto, long principalId);

    RegionDto deleteRegion(int regionId, long principalId);

    CountryDto createCountry(CountryDto dto, long principalId);

    CountryEvaluationDetailsDto createCountryEvaluationDetails(CountryEvaluationDetailsDto dto, int countryId, long principalId);

    CountryDto editCountry(CountryDto dto, int countryId, long principalId);

    CountryEvaluationDetailsDto editCountryEvaluationDetails(CountryEvaluationDetailsDto dto, int countryId, long principalId);

}
