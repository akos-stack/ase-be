package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.quotationpackage.*;

public interface IQuotationPackageFacade {

    ArrayQuotationPackageDataResponse allQuotationPackages();

    CreateQuotationPackageResponse createQuotationPackage(
            CreateQuotationPackageRequest request, long principalId);

}
