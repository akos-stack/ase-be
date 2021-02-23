package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.evaluation.PagedRegionsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
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
                                IEvaluationService evaluationService) {
        this.locationService = locationService;
        this.evaluationService = evaluationService;
    }

    @Override
    public PagedCountryEvaluationDetailsResponse searchCountryEvaluationDetails(SearchCountryEvaluationDetailsRequest request) {
        log.debug("EvaluationFacadeImpl.searchCountryEvaluationDetails - start | request: {}", request);
        var page = evaluationService.findAllCountryEvaluationDetails(request);
        var response = new PagedCountryEvaluationDetailsResponse(page.getContent(),
                page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages());
        log.debug("EvaluationFacadeImpl.searchCountryEvaluationDetails - end | request: {}", request);
        return response;
    }

    @Override
    public SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId) {
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - start | request: {}, principalId: {}", request, principalId);
        var detailsDto = doSaveCountryEvaluationDetails(request, principalId);
        var response = new SaveCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetailsId, long principalId) {
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - start | " +
                "request: {}, evaluationDetailsId {}, principalId: {}", request, evaluationDetailsId, principalId);
        var detailsDto = doUpdateCountryEvaluationDetails(request, evaluationDetailsId, principalId);
        var response = new UpdateCountryEvaluationDetailsResponse(detailsDto);
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - end | " +
                "request: {}, evaluationDetailsId {}, principalId: {}", request, evaluationDetailsId, principalId);
        return response;
    }

    private CountryEvaluationDetailsDto doUpdateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetailsId, long principalId) {
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setId(evaluationDetailsId);
        return evaluationService.updateCountryEvaluationDetails(evaluationDetailsDto, principalId);
    }

    @Override
    public PagedRegionsResponse searchRegions(SearchRegionsRequest request) {
        log.info("EvaluationFacadeImpl.searchRegions - start | request: {}", request);
        var page = evaluationService.findAllRegions(request);
        var response = new PagedRegionsResponse(page.getContent(),
                page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages());
        log.info("EvaluationFacadeImpl.searchRegions - end | request: {}", request);
        return response;
    }

    private CountryEvaluationDetailsDto doSaveCountryEvaluationDetails(SaveCountryEvaluationDetailsRequest request, long principalId) {
        var countryDto = locationService.findCountryByName(request.getCountry());
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setCountryId(countryDto.getId());
        return evaluationService.saveCountryEvaluationDetails(evaluationDetailsDto, principalId);
    }

}
