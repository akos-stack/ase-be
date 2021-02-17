package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_EXISTS;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class EvaluationServiceImpl implements IEvaluationService {

    private final CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Autowired
    public EvaluationServiceImpl(CountryEvaluationDetailsRepository countryEvaluationDetailsRepository) {
        this.countryEvaluationDetailsRepository = countryEvaluationDetailsRepository;
    }

    @Override
    public CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId) {
        log.debug("EvaluationServiceImpl.saveCountryEvaluationDetails - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        requireNotExists(dto);
        var details = MAPPER.toEntity(dto);
        details.setCreatorId(principalId);
        var detailsDto = MAPPER.toDto(countryEvaluationDetailsRepository.saveAndFlush(details));
        log.debug("EvaluationServiceImpl.saveCountryEvaluationDetails - end | dto: {}, principalId: {}", dto, principalId);
        return detailsDto;
    }

    private void requireNotExists(CountryEvaluationDetailsDto dto) {
        if (countryEvaluationDetailsRepository.findByCountryId(dto.getCountryId()).isPresent())
            throw COUNTRY_EVALUATION_DETAILS_EXISTS.newException();
    }

}
