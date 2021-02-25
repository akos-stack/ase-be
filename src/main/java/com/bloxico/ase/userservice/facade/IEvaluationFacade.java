package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.evaluation.PagedRegionsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;

public interface IEvaluationFacade {

    PagedCountryEvaluationDetailsResponse searchCountriesWithEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request);

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId);

    UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetails, long principalId);

    PagedCountryEvaluationDetailsResponse searchCountries(SearchCountryEvaluationDetailsRequest request);

    PagedRegionsResponse searchRegions(SearchRegionsRequest request);

}
