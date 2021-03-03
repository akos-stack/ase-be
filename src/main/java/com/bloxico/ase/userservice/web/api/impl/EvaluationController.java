package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.web.api.EvaluationApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvaluationController implements EvaluationApi {

    @Autowired
    private IEvaluationFacade evaluationFacade;

    @Override
    public ResponseEntity<SearchCountryEvaluationDetailsResponse> searchCountryEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request,
            PageRequest page)
    {
        var response = evaluationFacade.searchCountryEvaluationDetails(request, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchCountryEvaluationDetailsResponse> searchCountryEvaluationDetailsForManagement(
            SearchCountryEvaluationDetailsForManagementRequest request,
            PageRequest page)
    {
        var response = evaluationFacade.searchCountryEvaluationDetails(request, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveCountryEvaluationDetailsResponse> saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request)
    {
        var response = evaluationFacade.saveCountryEvaluationDetails(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UpdateCountryEvaluationDetailsResponse> updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request)
    {
        var response = evaluationFacade.updateCountryEvaluationDetails(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchRegionEvaluationDetailsResponse> searchRegionEvaluationDetailsForManagement(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest page)
    {
        var response = evaluationFacade.searchRegionEvaluationDetails(request, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveQuotationPackageResponse> saveQuotationPackage(
            SaveQuotationPackageRequest request)
    {
        var response = evaluationFacade.saveQuotationPackage(request);
        return ResponseEntity.ok(response);
    }

}
