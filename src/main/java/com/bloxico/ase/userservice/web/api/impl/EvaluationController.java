package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.web.api.EvaluationApi;
import com.bloxico.ase.userservice.web.model.evaluation.PagedRegionsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<PagedCountryEvaluationDetailsResponse> searchCountryEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request) {
        var response = evaluationFacade.searchCountriesWithEvaluationDetails(request);
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
            UpdateCountryEvaluationDetailsRequest request, Principal principal) {
        var id = extractId(principal);
        var response= evaluationFacade.updateCountryEvaluationDetails(request, id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedCountryEvaluationDetailsResponse> searchCountryEvaluationDetailsManagement(
            @Valid SearchCountryEvaluationDetailsRequest request) {
        var response = evaluationFacade.searchCountries(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedRegionsResponse> searchRegionsManagement(SearchRegionsRequest request) {
        var response = evaluationFacade.searchRegions(request);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<SaveQuotationPackageResponse> saveQuotationPackage(
            SaveQuotationPackageRequest request, Principal principal) {
        var id = extractId(principal);
        var response = evaluationFacade.saveQuotationPackage(request, id);
        return ResponseEntity.ok(response);
    }

}
