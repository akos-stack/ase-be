package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "evaluation")
public interface EvaluationApi {

    // @formatter:off
    String EVALUATION_COUNTRY_DETAILS_SEARCH            = "/evaluation/country-details";
    String EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SEARCH = "/evaluation/management/country-details";
    String EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE   = "/evaluation/management/country-details/save";
    String EVALUATION_MANAGEMENT_COUNTRY_DETAILS_UPDATE = "/evaluation/management/country-details/update";
    String EVALUATION_MANAGEMENT_COUNTRY_DETAILS_DELETE = "/evaluation/management/country-details/delete";
    String EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH  = "/evaluation/management/region-details";
    String EVALUATION_QUOTATION_PACKAGE_SAVE            = "/evaluation/quotation-package/save";
    String EVALUATION_EVALUATED_ARTWORKS_MY_SEARCH      = "/evaluation/evaluated-artworks/my";
    // @formatter:on

    @GetMapping(
            value = EVALUATION_COUNTRY_DETAILS_SEARCH,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_country_evaluation_details')")
    @ApiOperation(value = "Search countries with evaluation details.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of countries with evaluation details successfully retrieved.")
    })
    ResponseEntity<SearchCountryEvaluationDetailsResponse> searchCountryEvaluationDetails(
            @Valid SearchCountryEvaluationDetailsRequest request,
            @Valid PageRequest page);

    @GetMapping(
            value = EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SEARCH,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_management_country_evaluation_details')")
    @ApiOperation(value = "Search countries with evaluation details and those without also.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of countries with / without evaluation details successfully retrieved.")
    })
    ResponseEntity<SearchCountryEvaluationDetailsResponse> searchCountryEvaluationDetailsForManagement(
            @Valid SearchCountryEvaluationDetailsForManagementRequest request,
            @Valid PageRequest page);

    @PostMapping(
            value = EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE,
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
            @Valid @RequestBody SaveCountryEvaluationDetailsRequest request);

    @PostMapping(
            value = EVALUATION_MANAGEMENT_COUNTRY_DETAILS_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_country_evaluation_details')")
    @ApiOperation(value = "Updates evaluation details in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluation details successfully updated."),
            @ApiResponse(code = 404, message = "Specified evaluation details don't exist.")
    })
    ResponseEntity<UpdateCountryEvaluationDetailsResponse> updateCountryEvaluationDetails(
            @Valid @RequestBody UpdateCountryEvaluationDetailsRequest request);

    @PostMapping(
            value = EVALUATION_MANAGEMENT_COUNTRY_DETAILS_DELETE,
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_country_evaluation_details')")
    @ApiOperation(value = "Deletes evaluation details in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluation details successfully deleted."),
            @ApiResponse(code = 404, message = "Specified evaluation details don't exist."),
            @ApiResponse(code = 409, message = "There are evaluators from country to which evaluation details belong.")
    })
    ResponseEntity<Void> deleteCountryEvaluationDetails(@Valid @RequestBody DeleteCountryEvaluationDetailsRequest request);

    @GetMapping(
            value = EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_management_region_evaluation_details')")
    @ApiOperation(value = "Search regions with evaluation details.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of regions with evaluation details successfully retrieved.")
    })
    ResponseEntity<SearchRegionEvaluationDetailsResponse> searchRegionEvaluationDetailsForManagement(
            @Valid SearchRegionEvaluationDetailsRequest request,
            @Valid PageRequest page);

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
            @Valid @RequestBody SaveQuotationPackageRequest request);

    @GetMapping(value = EVALUATION_EVALUATED_ARTWORKS_MY_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_evaluated_artworks')")
    @ApiOperation(value = "Searches evaluated artworks.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluated artworks successfully searched.")
    })
    ResponseEntity<SearchEvaluatedArtworksResponse> searchMyEvaluatedArtworks(
            @Valid SearchEvaluatedArtworksRequest request, @Valid PageRequest page);

}
