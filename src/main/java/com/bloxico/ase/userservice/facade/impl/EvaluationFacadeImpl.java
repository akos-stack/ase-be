package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.config.IConfigService;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.entity.config.Config.Type.QUOTATION_PACKAGE_MIN_EVALUATIONS;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED;

@Slf4j
@Service
@Transactional
public class EvaluationFacadeImpl implements IEvaluationFacade {

    private final ILocationService locationService;
    private final IEvaluationService evaluationService;
    private final IConfigService configService;

    @Autowired
    public EvaluationFacadeImpl(ILocationService locationService,
                                IEvaluationService evaluationService,
                                IConfigService configService)
    {
        this.locationService = locationService;
        this.evaluationService = evaluationService;
        this.configService = configService;
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
    public void deleteCountryEvaluationDetails(Long evaluationDetailsId) {
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

    @Override
    public GetQuotationPackageMinEvaluationsResponse getQuotationPackageMinEvaluations() {
        log.debug("EvaluationFacadeImpl.getQuotationPackageMinEvaluations - start");
        var config = configService.findConfigByType(QUOTATION_PACKAGE_MIN_EVALUATIONS);
        var response = new GetQuotationPackageMinEvaluationsResponse(Integer.valueOf(config.getValue()));
        log.debug("EvaluationFacadeImpl.getQuotationPackageMinEvaluations - end");
        return response;
    }

    @Override
    public SetQuotationPackageMinEvaluationsResponse setQuotationPackageMinEvaluations(
            SetQuotationPackageMinEvaluationsRequest request) {
        log.debug("EvaluationFacadeImpl.setQuotationPackageMinEvaluations - start | request: {}", request);
        var dto = new ConfigDto();
        dto.setType(QUOTATION_PACKAGE_MIN_EVALUATIONS);
        dto.setValue(request.getMinEvaluations().toString());
        var evaluationConfigDto = configService.saveOrUpdateConfig(dto);
        var response = new SetQuotationPackageMinEvaluationsResponse(evaluationConfigDto);
        log.debug("EvaluationFacadeImpl.setQuotationPackageMinEvaluations - end | request: {}", request);
        return response;
    }

    private void requireCountryHasNoEvaluators(CountryDto countryDto) {
        var evaluatorsInCountry = evaluationService.countEvaluatorsByCountryId(countryDto.getId());
        if (evaluatorsInCountry > 0)
            throw COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED.newException();
    }

}
