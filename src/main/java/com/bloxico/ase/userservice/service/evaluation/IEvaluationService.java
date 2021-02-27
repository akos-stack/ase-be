package com.bloxico.ase.userservice.service.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageCountryDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Set;

public interface IEvaluationService {

    Page<CountryEvaluationDetailsWithEvaluatorsCountProj> findAllCountriesWithEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request);

    CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId);

    QuotationPackageDto saveQuotationPackage(QuotationPackageDto quotationPackage, long principalId);

    Set<QuotationPackageCountryDto> saveQuotationPackageCountries(long packageId,
                                                                  Collection<QuotationPackageCountryDto> countries,
                                                                  long principalId);

    CountryEvaluationDetailsDto updateCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId);

    Page<CountryEvaluationDetailsWithEvaluatorsCountProj> findAllCountries(
            SearchCountryEvaluationDetailsRequest request);

    Page<RegionWithCountriesAndEvaluatorsCountProj> findAllRegions(SearchRegionsRequest request);

}
