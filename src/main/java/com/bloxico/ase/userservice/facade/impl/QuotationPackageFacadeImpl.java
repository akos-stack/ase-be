package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IQuotationPackageFacade;
import com.bloxico.ase.userservice.service.quotationpackage.IQuotationPackageService;
import com.bloxico.ase.userservice.web.model.quotationpackage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
public class QuotationPackageFacadeImpl implements IQuotationPackageFacade {

    private final IQuotationPackageService quotationPackageService;

    @Autowired
    public QuotationPackageFacadeImpl(
            IQuotationPackageService quotationPackageService) {

        this.quotationPackageService = quotationPackageService;
    }

    @Override
    public ArrayQuotationPackageDataResponse allQuotationPackages() {

        log.info("QuotationPackageFacadeImpl.allQuotationPackages - start");

        var quotationPackages = quotationPackageService
                .findAll();

        var response = new ArrayQuotationPackageDataResponse(quotationPackages);

        log.info("QuotationPackageFacadeImpl.allQuotationPackages - end");
        return response;
    }

    @Override
    public CreateQuotationPackageResponse createQuotationPackage(
            CreateQuotationPackageRequest request, long principalId) {

        log.info("QuotationPackageFacadeImpl.createQuotationPackage - start | request: {}, principalId: {}",
                request, principalId);

        var quotationPackage = quotationPackageService
                .createQuotationPackage(request, principalId);

        var response = MAPPER.toResponse(quotationPackage);

        log.info("QuotationPackageFacadeImpl.createQuotationPackage - end | request: {}, principalId: {}",
                request, principalId);
        return response;
    }

}
