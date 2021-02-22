package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import com.bloxico.ase.userservice.proj.RegionCountedProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_EXISTS;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
public class EvaluationServiceImpl implements IEvaluationService {

    private final CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Autowired
    public EvaluationServiceImpl(CountryEvaluationDetailsRepository countryEvaluationDetailsRepository) {
        this.countryEvaluationDetailsRepository = countryEvaluationDetailsRepository;
    }

    @Override
    public Page<CountryEvaluationDetailsCountedProj> findAllCountryEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request) {
        log.debug("EvaluationServiceImpl.findAllCountryEvaluationDetails - start | request: {}", request);
        requireNonNull(request);
        var page = countryEvaluationDetailsRepository
                .findAllCountryEvaluationDetailsWithEvaluatorsCount(
                        request.getSearch(), request.getRegions(), getPagination(request));
        log.debug("EvaluationServiceImpl.findAllCountryEvaluationDetails - end | request: {}", request);
        return page;
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

    @Override
    public CountryEvaluationDetailsDto updateCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId) {
        log.debug("EvaluationServiceImpl.updateCountryEvaluationDetails - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var details = countryEvaluationDetailsRepository
                .findById(dto.getId())
                .orElseThrow(COUNTRY_EVALUATION_DETAILS_NOT_FOUND::newException);
        details.setUpdaterId(principalId);
        details.setPricePerEvaluation(dto.getPricePerEvaluation());
        details.setAvailabilityPercentage(dto.getAvailabilityPercentage());
        var detailsDto = MAPPER.toDto(countryEvaluationDetailsRepository.saveAndFlush(details));
        log.debug("EvaluationServiceImpl.updateCountryEvaluationDetails - end | dto: {}, principalId: {}", dto, principalId);
        return detailsDto;
    }

    @Override
    public Page<RegionCountedProj> findAllRegions(SearchRegionsRequest request) {
        log.debug("EvaluationServiceImpl.findAllRegions - start | request: {}", request);
        requireNonNull(request);
        var page = countryEvaluationDetailsRepository
                .findAllIncludeCountriesAndEvaluatorsCount(request.getSearch(), getPagination(request));
        log.debug("EvaluationServiceImpl.findAllRegions - end | request: {}", request);
        return page;
    }

    private void requireNotExists(CountryEvaluationDetailsDto dto) {
        if (countryEvaluationDetailsRepository.findByCountryId(dto.getCountryId()).isPresent())
            throw COUNTRY_EVALUATION_DETAILS_EXISTS.newException();
    }

    private Pageable getPagination(SearchCountryEvaluationDetailsRequest request) {
        var unsafeSortProperty =  String.format("(%s)", request.getSort());
        var sort = request.getOrder().equals("asc") ?
                JpaSort.unsafe(ASC, unsafeSortProperty) :
                JpaSort.unsafe(DESC, unsafeSortProperty);
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private Pageable getPagination(SearchRegionsRequest request) {
        var unsafeSortProperty =  String.format("(%s)", request.getSort());
        var sort = request.getOrder().equals("asc") ?
                JpaSort.unsafe(ASC, unsafeSortProperty) :
                JpaSort.unsafe(DESC, unsafeSortProperty);
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

}
