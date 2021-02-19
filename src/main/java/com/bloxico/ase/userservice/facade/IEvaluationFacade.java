package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsResponse;

public interface IEvaluationFacade {

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId);

    UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetails, long principalId);

}
