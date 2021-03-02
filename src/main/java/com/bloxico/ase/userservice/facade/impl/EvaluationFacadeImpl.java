package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
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
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED;

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
            SaveCountryEvaluationDetailsRequest request, long principalId)
    {
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - start | request: {}, principalId: {}", request, principalId);
        var countryDto = locationService.findCountryByName(request.getCountry());
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setCountryId(countryDto.getId());
        var detailsDto = evaluationService.saveCountryEvaluationDetails(evaluationDetailsDto, principalId);
        var response = new SaveCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, long principalId)
    {
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - start | request: {}, principalId: {}", request, principalId);
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        var detailsDto = evaluationService.updateCountryEvaluationDetails(evaluationDetailsDto, principalId);
        var response = new UpdateCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void deleteCountryEvaluationDetails(int evaluationDetailsId) {
        log.debug("EvaluationFacadeImpl.deleteCountryEvaluationDetails - start | evaluationDetailsId: {}", evaluationDetailsId);
        var detailsDto = evaluationService.findCountryEvaluationDetailsById(evaluationDetailsId);
        var countryDto = locationService.findCountryById(detailsDto.getCountryId());
        requireCountryHasNoEvaluators(countryDto);
        evaluationService.deleteCountryEvaluationDetails(detailsDto);
        log.debug("EvaluationFacadeImpl.deleteCountryEvaluationDetails - end | evaluationDetailsId: {}", evaluationDetailsId);
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
    public SaveQuotationPackageResponse saveQuotationPackage(SaveQuotationPackageRequest request, long principalId) {
        log.debug("EvaluationFacadeImpl.saveQuotationPackage - start | request: {}, principalId: {}", request, principalId);
        var quotationPackageDto = MAPPER.toQuotationPackageDto(request);
        var quotationPackage = evaluationService.saveQuotationPackage(quotationPackageDto, principalId);
        var countries = evaluationService.saveQuotationPackageCountries(
                quotationPackage.getId(),
                quotationPackageDto.getCountries(),
                principalId);
        quotationPackage.setCountries(countries);
        var response = new SaveQuotationPackageResponse(quotationPackage);
        log.debug("EvaluationFacadeImpl.saveQuotationPackage - start | request: {}, principalId: {}", request, principalId);
        return response;
    }

    private void requireCountryHasNoEvaluators(CountryDto countryDto) {
        var evaluatorsInCountry = evaluationService.countEvaluatorsByCountryId(countryDto.getId());
        if (evaluatorsInCountry > 0)
            throw COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED.newException();
    }

}
