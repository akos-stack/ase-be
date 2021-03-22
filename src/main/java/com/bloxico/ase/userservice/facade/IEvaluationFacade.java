package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;

public interface IEvaluationFacade {

    SearchCountryEvaluationDetailsResponse searchCountryEvaluationDetails(
            ISearchCountryEvaluationDetailsRequest request,
            PageRequest page);

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request);

    UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request);

    void deleteCountryEvaluationDetails(Long evaluationDetailsId);

    SearchRegionEvaluationDetailsResponse searchRegionEvaluationDetails(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest page);

    SaveQuotationPackageResponse saveQuotationPackage(
            SaveQuotationPackageRequest request);

    SearchEvaluableArtworksResponse searchEvaluableArtworks(SearchEvaluableArtworksRequest request, PageRequest page);

}
