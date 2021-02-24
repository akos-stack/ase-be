package com.bloxico.ase.userservice.service.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.*;

import java.util.Collection;
import java.util.Set;

public interface IEvaluationService {

    CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto details, long principalId);

    QuotationPackageDto saveQuotationPackage(QuotationPackageDto quotationPackage, long principalId);

    Set<QuotationPackageCountryDto> saveQuotationPackageCountries(long packageId,
                                                                  Collection<QuotationPackageCountryDto> countries,
                                                                  long principalId);

}
