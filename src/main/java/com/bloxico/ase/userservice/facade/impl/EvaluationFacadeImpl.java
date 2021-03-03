package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
@Transactional
public class EvaluationFacadeImpl implements IEvaluationFacade {

    private final ILocationService locationService;
    private final IEvaluationService evaluationService;

    @Autowired
    public EvaluationFacadeImpl(ILocationService locationService,
                                IEvaluationService evaluationService)
    {
        this.locationService = locationService;
        this.evaluationService = evaluationService;
    }

    @Override
    public SearchCountryEvaluationDetailsResponse searchCountryEvaluationDetails(
            ISearchCountryEvaluationDetailsRequest request,
            PageRequest pageDetails)
    {
        log.debug("EvaluationFacadeImpl.searchCountriesWithEvaluationDetails - start | request: {}, pageDetails: {}", request, pageDetails);
        var page = evaluationService.searchCountryEvaluationDetails(request, pageDetails);
        var response = new SearchCountryEvaluationDetailsResponse(page);
        log.debug("EvaluationFacadeImpl.searchCountriesWithEvaluationDetails - end | request: {}, pageDetails: {}", request, pageDetails);
        return response;
    }

    @Override
    public SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request)
    {
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - start | request: {}", request);
        var countryDto = locationService.findCountryByName(request.getCountry());
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setCountryId(countryDto.getId());
        var detailsDto = evaluationService.saveCountryEvaluationDetails(evaluationDetailsDto);
        var response = new SaveCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - end | request: {}", request);
        return response;
    }

    @Override
    public UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request)
    {
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - start | request: {}", request);
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        var detailsDto = evaluationService.updateCountryEvaluationDetails(evaluationDetailsDto);
        var response = new UpdateCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - end | request: {}", request);
        return response;
    }

    @Override
    public SearchRegionEvaluationDetailsResponse searchRegionEvaluationDetails(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest pageDetails)
    {
        log.info("EvaluationFacadeImpl.searchRegions - start | request: {}, pageDetails: {}", request, pageDetails);
        var page = evaluationService.searchRegionEvaluationDetails(request, pageDetails);
        var response = new SearchRegionEvaluationDetailsResponse(page);
        log.info("EvaluationFacadeImpl.searchRegions - end | request: {}, pageDetails: {}", request, pageDetails);
        return response;
    }

    @Override
    public SaveQuotationPackageResponse saveQuotationPackage(SaveQuotationPackageRequest request) {
        log.debug("EvaluationFacadeImpl.saveQuotationPackage - start | request: {}", request);
        var quotationPackageDto = MAPPER.toQuotationPackageDto(request);
        var quotationPackage = evaluationService.saveQuotationPackage(quotationPackageDto);
        var countries = evaluationService.saveQuotationPackageCountries(
                quotationPackage.getId(),
                quotationPackageDto.getCountries());
        quotationPackage.setCountries(countries);
        var response = new SaveQuotationPackageResponse(quotationPackage);
        log.debug("EvaluationFacadeImpl.saveQuotationPackage - start | request: {}", request);
        return response;
    }

}
