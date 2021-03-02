package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;

public interface IEvaluationFacade {

    SearchCountryEvaluationDetailsResponse searchCountryEvaluationDetails(
            ISearchCountryEvaluationDetailsRequest request,
            PageRequest page);

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request,
            long principalId);

    UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, long principalId);

    SearchRegionEvaluationDetailsResponse searchRegionEvaluationDetails(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest page);

    SaveQuotationPackageResponse saveQuotationPackage(
            SaveQuotationPackageRequest request,
            long principalId);

}
