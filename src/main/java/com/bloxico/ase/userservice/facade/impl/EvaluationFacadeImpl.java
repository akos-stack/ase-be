package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.config.IConfigService;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
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
            PageRequest page)
    {
        log.debug("EvaluationFacadeImpl.searchCountriesWithEvaluationDetails - start | request: {}, page: {}", request, page);
        var result = evaluationService.searchCountryEvaluationDetails(request, page);
        var response = new SearchCountryEvaluationDetailsResponse(result);
        log.debug("EvaluationFacadeImpl.searchCountriesWithEvaluationDetails - end | request: {}, page: {}", request, page);
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
            PageRequest page)
    {
        log.info("EvaluationFacadeImpl.searchRegions - start | request: {}, page: {}", request, page);
        var result = evaluationService.searchRegionEvaluationDetails(request, page);
        var response = new SearchRegionEvaluationDetailsResponse(result);
        log.info("EvaluationFacadeImpl.searchRegions - end | request: {}, page: {}", request, page);
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
    public SearchEvaluatedArtworksResponse searchEvaluatedArtworks(
            WithOwner<SearchEvaluatedArtworksRequest> withOwner, PageRequest page)
    {
        log.debug("EvaluationFacadeImpl.searchEvaluatedArtworks - start | withOwner: {}, page: {}", withOwner, page);
        var result = evaluationService.searchEvaluatedArtworks(withOwner, page);
        var response = new SearchEvaluatedArtworksResponse(result);
        log.debug("EvaluationFacadeImpl.searchEvaluatedArtworks - end | withOwner: {}, page: {}", withOwner, page);
        return response;
    }

    private void requireCountryHasNoEvaluators(CountryDto countryDto) {
        var evaluatorsInCountry = evaluationService.countEvaluatorsByCountryId(countryDto.getId());
        if (evaluatorsInCountry > 0)
            throw COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED.newException();
    }

}
