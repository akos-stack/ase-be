package com.bloxico.ase.userservice.service.quotationpackage;

import com.bloxico.ase.userservice.dto.entity.quotationpackage.QuotationPackageDto;
import com.bloxico.ase.userservice.web.model.quotationpackage.CreateQuotationPackageRequest;

import java.util.List;

public interface IQuotationPackageService {

    List<QuotationPackageDto> findAll();

    QuotationPackageDto createQuotationPackage(
            CreateQuotationPackageRequest request, long principalId);

}
