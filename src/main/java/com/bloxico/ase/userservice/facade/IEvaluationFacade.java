package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;

public interface IEvaluationFacade {

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId);

}
