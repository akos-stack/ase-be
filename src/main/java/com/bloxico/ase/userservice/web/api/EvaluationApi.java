package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "evaluation")
public interface EvaluationApi {

    String EVALUATION_COUNTRY_DETAILS_SAVE = "/evaluation/country-details/save";
    String EVALUATION_COUNTRY_DETAILS_UPDATE = "/evaluation/country-details/update/{id}";

    @PostMapping(
            value = EVALUATION_COUNTRY_DETAILS_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_country_evaluation_details')")
    @ApiOperation(value = "Saves evaluation details for specified country in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluation details for specified country successfully saved."),
            @ApiResponse(code = 404, message = "Specified country doesn't exist."),
            @ApiResponse(code = 409, message = "Evaluation details already exists for specified country.")
    })
    ResponseEntity<SaveCountryEvaluationDetailsResponse> saveCountryEvaluationDetails(
            @Valid @RequestBody SaveCountryEvaluationDetailsRequest request, Principal principal);

    @PostMapping(
            value = EVALUATION_COUNTRY_DETAILS_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_country_evaluation_details')")
    @ApiOperation(value = "Updates evaluation details in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluation details successfully updated."),
            @ApiResponse(code = 404, message = "Specified evaluation details don't exist.")
    })
    ResponseEntity<UpdateCountryEvaluationDetailsResponse> updateCountryEvaluationDetails(
            @Valid @RequestBody UpdateCountryEvaluationDetailsRequest request,
            @Valid @PathVariable("id") Integer evaluationDetailsId, Principal principal);

}
