package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.quotationpackage.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "quotationPackage")
public interface QuotationPackageApi {

    String QUOTATION_PACKAGES_ENDPOINT = "/quotationPackages";
    String QUOTATION_PACKAGE_CREATE_ENDPOINT = "/quotationPackages/create";

    @GetMapping(value = QUOTATION_PACKAGES_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_quotation_packages')")
    @ApiOperation(value = "Fetch all quotation packages.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List of quotation packages successfully retrieved.")
    })
    ResponseEntity<ArrayQuotationPackageDataResponse> allQuotationPackages();

    @PostMapping(
            value = QUOTATION_PACKAGE_CREATE_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'create_quotation_package')")
    @ApiOperation(value = "Creates new quotation package.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Quotation package successfully created."),
            @ApiResponse(code = 409, message = "Quotation package already exists.")
    })
    ResponseEntity<CreateQuotationPackageResponse> createQuotationPackage(
            @Valid @RequestBody CreateQuotationPackageRequest request, Principal principal);

}
