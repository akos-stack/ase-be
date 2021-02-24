package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.evaluation.*;

public interface IEvaluationFacade {

    SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request,
            long principalId);

    SaveQuotationPackageResponse saveQuotationPackage(
            SaveQuotationPackageRequest request,
            long principalId);

}
