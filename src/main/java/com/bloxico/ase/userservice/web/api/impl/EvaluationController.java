package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.web.api.EvaluationApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

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
            SaveCountryEvaluationDetailsRequest request, Principal principal)
    {
        var id = extractId(principal);
        var response = evaluationFacade.saveCountryEvaluationDetails(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UpdateCountryEvaluationDetailsResponse> updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, Principal principal)
    {
        var id = extractId(principal);
        var response = evaluationFacade.updateCountryEvaluationDetails(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteCountryEvaluationDetails(@Valid DeleteCountryEvaluationDetailsRequest request) {
        evaluationFacade.deleteCountryEvaluationDetails(request.getId());
        return new ResponseEntity<>(HttpStatus.OK);
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
            SaveQuotationPackageRequest request, Principal principal)
    {
        var id = extractId(principal);
        var response = evaluationFacade.saveQuotationPackage(request, id);
        return ResponseEntity.ok(response);
    }

}
