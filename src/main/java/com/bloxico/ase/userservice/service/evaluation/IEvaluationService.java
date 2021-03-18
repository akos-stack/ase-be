package com.bloxico.ase.userservice.service.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageCountryDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.bloxico.ase.userservice.proj.evaluation.ArtworkEvaluatedProj;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.evaluation.ISearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchEvaluatedArtworksRequest;
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

    Page<ArtworkEvaluatedProj> searchEvaluatedArtworks(
            WithOwner<SearchEvaluatedArtworksRequest> withOwner, PageRequest page);

}
