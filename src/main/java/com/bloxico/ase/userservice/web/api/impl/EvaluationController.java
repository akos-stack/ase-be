package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.web.api.EvaluationApi;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class EvaluationController implements EvaluationApi {

    @Autowired
    private IEvaluationFacade evaluationFacade;

    @Override
    public ResponseEntity<SaveCountryEvaluationDetailsResponse> saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, Principal principal)
    {
        var id = extractId(principal);
        var response = evaluationFacade.saveCountryEvaluationDetails(request, id);
        return ResponseEntity.ok(response);
    }

}
