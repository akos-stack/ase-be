package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.evaluation.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "evaluation")
public interface EvaluationApi {

    String EVALUATION_COUNTRY_DETAILS_SAVE   = "/evaluation/country-details/save";
    String EVALUATION_QUOTATION_PACKAGE_SAVE = "/evaluation/quotation-package/save";

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
            value = EVALUATION_QUOTATION_PACKAGE_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_quotation_package')")
    @ApiOperation(value = "Saves quotation package for specified artwork in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Quotation package for specified artwork successfully saved."),
            @ApiResponse(code = 404, message = "Specified artwork doesn't exist."), // TODO
            @ApiResponse(code = 409, message = "Quotation package already exists for specified artwork.")
    })
    ResponseEntity<SaveQuotationPackageResponse> saveQuotationPackage(
            @Valid @RequestBody SaveQuotationPackageRequest request, Principal principal);

}
