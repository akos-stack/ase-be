package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IQuotationPackageFacade;
import com.bloxico.ase.userservice.web.api.QuotationPackageApi;
import com.bloxico.ase.userservice.web.model.quotationpackage.ArrayQuotationPackageDataResponse;
import com.bloxico.ase.userservice.web.model.quotationpackage.CreateQuotationPackageRequest;
import com.bloxico.ase.userservice.web.model.quotationpackage.CreateQuotationPackageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class QuotationPackageController implements QuotationPackageApi {

    private final IQuotationPackageFacade quotationPackageFacade;

    @Autowired
    public QuotationPackageController(IQuotationPackageFacade quotationPackageFacade) {
        this.quotationPackageFacade = quotationPackageFacade;
    }

    @Override
    public ResponseEntity<ArrayQuotationPackageDataResponse> allQuotationPackages() {

        var response = quotationPackageFacade
                .allQuotationPackages();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CreateQuotationPackageResponse> createQuotationPackage(
            @Valid @RequestBody CreateQuotationPackageRequest request, Principal principal) {

        var response = quotationPackageFacade
                .createQuotationPackage(request, extractId(principal));

        return ResponseEntity.ok(response);
    }

}
