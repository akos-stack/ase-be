package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageCountryDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.evaluation.QuotationPackageCountryRepository;
import com.bloxico.ase.userservice.repository.evaluation.QuotationPackageRepository;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
public class EvaluationServiceImpl implements IEvaluationService {

    private final CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    private final QuotationPackageRepository quotationPackageRepository;
    private final QuotationPackageCountryRepository quotationPackageCountryRepository;

    @Autowired
    public EvaluationServiceImpl(CountryEvaluationDetailsRepository countryEvaluationDetailsRepository,
                                 QuotationPackageRepository quotationPackageRepository,
                                 QuotationPackageCountryRepository quotationPackageCountryRepository) {
        this.countryEvaluationDetailsRepository = countryEvaluationDetailsRepository;
        this.quotationPackageRepository = quotationPackageRepository;
        this.quotationPackageCountryRepository = quotationPackageCountryRepository;
    }

    @Override
    public Page<CountryEvaluationDetailsWithEvaluatorsCountProj> findAllCountriesWithEvaluationDetails(
            SearchCountryEvaluationDetailsRequest request) {
        log.debug("EvaluationServiceImpl.findAllCountriesWithEvaluationDetails - start | request: {}", request);
        requireNonNull(request);
        var page = countryEvaluationDetailsRepository
                .findAllCountryEvaluationDetailsWithEvaluatorsCount(request.getSearch(),
                        request.getRegions(), false, getPagination(request));
        log.debug("EvaluationServiceImpl.findAllCountriesWithEvaluationDetails - end | request: {}", request);
        return page.map(MAPPER::toCountedProj);
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
    public Page<CountryEvaluationDetailsWithEvaluatorsCountProj> findAllCountries(
            SearchCountryEvaluationDetailsRequest request) {
        log.debug("EvaluationServiceImpl.findAllCountries - start | request: {}", request);
        requireNonNull(request);
        var page = countryEvaluationDetailsRepository
                .findAllCountryEvaluationDetailsWithEvaluatorsCount(request.getSearch(),
                        request.getRegions(), true, getPagination(request));
        log.debug("EvaluationServiceImpl.findAllCountries - end | request: {}", request);
        return page.map(MAPPER::toCountedProj);
    }

    @Override
    public Page<RegionWithCountriesAndEvaluatorsCountProj> findAllRegions(SearchRegionsRequest request) {
        log.debug("EvaluationServiceImpl.findAllRegions - start | request: {}", request);
        requireNonNull(request);
        var page = countryEvaluationDetailsRepository
                .findAllRegionsWithCountriesAndEvaluatorsCount(request.getSearch(), getPagination(request));
        log.debug("EvaluationServiceImpl.findAllRegions - end | request: {}", request);
        return page;
    }

    public QuotationPackageDto saveQuotationPackage(QuotationPackageDto dto, long principalId) {
        log.debug("EvaluationServiceImpl.saveQuotationPackageDto - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        requireNotExists(dto);
        var quotationPackage = MAPPER.toEntity(dto);
        quotationPackage.setCreatorId(principalId);
        quotationPackageRepository.saveAndFlush(quotationPackage);
        var quotationPackageDto = MAPPER.toDto(quotationPackage);
        quotationPackageDto.setCountries(dto.getCountries());
        log.debug("EvaluationServiceImpl.saveQuotationPackageDto - start | dto: {}, principalId: {}", dto, principalId);
        return quotationPackageDto;
    }

    @Override
    public Set<QuotationPackageCountryDto> saveQuotationPackageCountries(long packageId,
                                                                         Collection<QuotationPackageCountryDto> dtos,
                                                                         long principalId) {
        log.debug("EvaluationServiceImpl.saveQuotationPackageCountries - start | packageId: {}, dtos: {}, principalId: {}",
                packageId, dtos, principalId);
        requireNonNull(dtos);
        var quotationPackageCountryDtos = dtos
                .stream()
                .map(doto(this::requireNotExists))
                .map(doto(qpc -> qpc.setQuotationPackageId(packageId)))
                .map(MAPPER::toEntity)
                .map(doto(qpc -> qpc.setCreatorId(principalId)))
                .map(quotationPackageCountryRepository::saveAndFlush)
                .map(MAPPER::toDto)
                .collect(toSet());
        log.debug("EvaluationServiceImpl.saveQuotationPackageCountries - end | packageId: {}, dtos: {}, principalId: {}",
                packageId, dtos, principalId);
        return quotationPackageCountryDtos;
    }

    private void requireNotExists(CountryEvaluationDetailsDto dto) {
        if (countryEvaluationDetailsRepository.findByCountryId(dto.getCountryId()).isPresent())
            throw COUNTRY_EVALUATION_DETAILS_EXISTS.newException();
    }

    private Pageable getPagination(SearchCountryEvaluationDetailsRequest request) {
        var unsafeSortProperty = String.format("(%s)", request.getSort());
        var sort = request.getOrder().equals("asc") ?
                JpaSort.unsafe(ASC, unsafeSortProperty) :
                JpaSort.unsafe(DESC, unsafeSortProperty);
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private Pageable getPagination(SearchRegionsRequest request) {
        var unsafeSortProperty = String.format("(%s)", request.getSort());
        var sort = request.getOrder().equals("asc") ?
                JpaSort.unsafe(ASC, unsafeSortProperty) :
                JpaSort.unsafe(DESC, unsafeSortProperty);
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private void requireNotExists(QuotationPackageDto dto) {
        if (quotationPackageRepository.findByArtworkId(dto.getArtworkId()).isPresent())
            throw QUOTATION_PACKAGE_EXISTS.newException();
    }

    private void requireNotExists(QuotationPackageCountryDto dto) {
        if (quotationPackageCountryRepository.findByIdCountryId(dto.getCountryId()).isPresent())
            throw QUOTATION_PACKAGE_COUNTRY_EXISTS.newException();
    }
}
