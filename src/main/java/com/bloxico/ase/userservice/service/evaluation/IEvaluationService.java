package com.bloxico.ase.userservice.service.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.EvaluableArtworkProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.ISearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchEvaluableArtworksRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsRequest;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Set;

public interface IEvaluationService {

    CountryEvaluationDetailsDto findCountryEvaluationDetailsById(Long id);

    int countEvaluatorsByCountryId(Long countryId);

    Page<CountryEvaluationDetailsWithEvaluatorsCountProj> searchCountryEvaluationDetails(
            ISearchCountryEvaluationDetailsRequest request,
            PageRequest page);

    CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto dto);

    CountryEvaluationDetailsDto updateCountryEvaluationDetails(CountryEvaluationDetailsDto dto);

    CountryEvaluationDetailsDto deleteCountryEvaluationDetails(CountryEvaluationDetailsDto detailsDto);

    Page<RegionWithCountriesAndEvaluatorsCountProj> searchRegionEvaluationDetails(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest page);

    QuotationPackageDto saveQuotationPackage(QuotationPackageDto quotationPackage);

    Set<QuotationPackageCountryDto> saveQuotationPackageCountries(
            long packageId, Collection<QuotationPackageCountryDto> countries);

    Page<EvaluableArtworkProj> searchEvaluableArtworks(SearchEvaluableArtworksRequest request, PageRequest pageRequest);

}
