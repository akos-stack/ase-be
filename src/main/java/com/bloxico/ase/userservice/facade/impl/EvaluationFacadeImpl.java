package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IEvaluationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsResponse;
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
    public SaveCountryEvaluationDetailsResponse saveCountryEvaluationDetails(
            SaveCountryEvaluationDetailsRequest request, long principalId) {
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - start | request: {}, principalId: {}", request, principalId);
        var countryDto = locationService.findCountryByName(request.getCountry());
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setCountryId(countryDto.getId());
        evaluationDetailsDto = evaluationService.saveCountryEvaluationDetails(evaluationDetailsDto, principalId);
        var response = new SaveCountryEvaluationDetailsResponse(evaluationDetailsDto);
        log.debug("EvaluationFacadeImpl.saveCountryEvaluationDetails - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public UpdateCountryEvaluationDetailsResponse updateCountryEvaluationDetails(
            UpdateCountryEvaluationDetailsRequest request, int evaluationDetailsId, long principalId) {
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - start | " +
                "request: {}, evaluationDetailsId {}, principalId: {}", request, evaluationDetailsId, principalId);
        var evaluationDetailsDto = MAPPER.toCountryEvaluationDetailsDto(request);
        evaluationDetailsDto.setId(evaluationDetailsId);
        evaluationDetailsDto = evaluationService.updateCountryEvaluationDetails(evaluationDetailsDto, principalId);
        var response = new UpdateCountryEvaluationDetailsResponse(evaluationDetailsDto);
        log.debug("EvaluationFacadeImpl.updateCountryEvaluationDetails - end | " +
                "request: {}, evaluationDetailsId {}, principalId: {}", request, evaluationDetailsId, principalId);
        return response;
    }

}
