package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.repository.evaluation.*;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class EvaluationServiceImpl implements IEvaluationService {

    private final CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    private final QuotationPackageRepository quotationPackageRepository;
    private final QuotationPackageCountryRepository quotationPackageCountryRepository;

    @Autowired
    public EvaluationServiceImpl(CountryEvaluationDetailsRepository countryEvaluationDetailsRepository,
                                 QuotationPackageRepository quotationPackageRepository,
                                 QuotationPackageCountryRepository quotationPackageCountryRepository)
    {
        this.countryEvaluationDetailsRepository = countryEvaluationDetailsRepository;
        this.quotationPackageRepository = quotationPackageRepository;
        this.quotationPackageCountryRepository = quotationPackageCountryRepository;
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
                                                                         long principalId)
    {
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

    private void requireNotExists(QuotationPackageDto dto) {
        if (quotationPackageRepository.findByArtworkId(dto.getArtworkId()).isPresent())
            throw QUOTATION_PACKAGE_EXISTS.newException();
    }

    private void requireNotExists(QuotationPackageCountryDto dto) {
        if (quotationPackageCountryRepository.findByIdCountryId(dto.getCountryId()).isPresent())
            throw QUOTATION_PACKAGE_COUNTRY_EXISTS.newException();
    }

}
