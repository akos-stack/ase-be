package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.evaluation.*;

public interface IEvaluationFacade {

    PagedCountryEvaluationDetailsResponse searchCountryEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request);

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId);

    UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetails, long principalId);

}
