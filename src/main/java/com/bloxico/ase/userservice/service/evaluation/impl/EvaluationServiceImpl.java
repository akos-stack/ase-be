package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.repository.evaluation.*;
import com.bloxico.ase.userservice.service.evaluation.IEvaluationService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.evaluation.ISearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public CountryEvaluationDetailsDto findCountryEvaluationDetailsById(Long id) {
        log.debug("EvaluationServiceImpl.findCountryEvaluationDetailsById - start | id: {}", id);
        var detailsDto = countryEvaluationDetailsRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(COUNTRY_EVALUATION_DETAILS_NOT_FOUND::newException);
        log.debug("EvaluationServiceImpl.findCountryEvaluationDetailsById - end | id: {}", id);
        return detailsDto;
    }

    @Override
    public int countEvaluatorsByCountryId(Long countryId) {
        log.debug("EvaluationServiceImpl.countEvaluatorsByCountryId - start | countryId: {}", countryId);
        var count = countryEvaluationDetailsRepository.countEvaluatorsByCountryId(countryId);
        log.debug("EvaluationServiceImpl.countEvaluatorsByCountryId - end | countryId: {}", countryId);
        return count;
    }

    @Override
    public Page<CountryEvaluationDetailsWithEvaluatorsCountProj> searchCountryEvaluationDetails(
            ISearchCountryEvaluationDetailsRequest request,
            PageRequest pageDetails)
    {
        log.debug("EvaluationServiceImpl.searchCountryEvaluationDetails - start | request: {}, pageDetails: {}", request, pageDetails);
        requireNonNull(request);
        requireNonNull(pageDetails);
        var page = countryEvaluationDetailsRepository
                .findAllCountryEvaluationDetailsWithEvaluatorsCount(
                        request.getSearch(),
                        request.getRegions(),
                        request.includeCountriesWithoutEvaluationDetails(),
                        pageDetails.toPageable())
                .map(MAPPER::toCountedProj);
        log.debug("EvaluationServiceImpl.searchCountryEvaluationDetails - end | request: {}, pageDetails: {}", request, pageDetails);
        return page;
    }

    @Override
    public CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto dto) {
        log.debug("EvaluationServiceImpl.saveCountryEvaluationDetails - start | dto: {}", dto);
        requireNonNull(dto);
        requireNotExists(dto);
        var details = MAPPER.toEntity(dto);
        var detailsDto = MAPPER.toDto(countryEvaluationDetailsRepository.saveAndFlush(details));
        log.debug("EvaluationServiceImpl.saveCountryEvaluationDetails - end | dto: {}", dto);
        return detailsDto;
    }

    @Override
    public CountryEvaluationDetailsDto updateCountryEvaluationDetails(CountryEvaluationDetailsDto dto) {
        log.debug("EvaluationServiceImpl.updateCountryEvaluationDetails - start | dto: {}", dto);
        requireNonNull(dto);
        var details = countryEvaluationDetailsRepository
                .findById(dto.getId())
                .orElseThrow(COUNTRY_EVALUATION_DETAILS_NOT_FOUND::newException);
        details.setPricePerEvaluation(dto.getPricePerEvaluation());
        details.setAvailabilityPercentage(dto.getAvailabilityPercentage());
        var detailsDto = MAPPER.toDto(countryEvaluationDetailsRepository.saveAndFlush(details));
        log.debug("EvaluationServiceImpl.updateCountryEvaluationDetails - end | dto: {}", dto);
        return detailsDto;
    }

    @Override
    public CountryEvaluationDetailsDto deleteCountryEvaluationDetails(CountryEvaluationDetailsDto dto) {
        log.debug("EvaluationServiceImpl.deleteCountryEvaluationDetails - start | dto: {}", dto);
        requireNonNull(dto);
        var details = MAPPER.toEntity(dto);
        countryEvaluationDetailsRepository.delete(details);
        var detailsDto = MAPPER.toDto(details);
        log.debug("EvaluationServiceImpl.deleteCountryEvaluationDetails - end | dto: {}", dto);
        return detailsDto;
    }

    @Override
    public Page<RegionWithCountriesAndEvaluatorsCountProj> searchRegionEvaluationDetails(
            SearchRegionEvaluationDetailsRequest request,
            PageRequest pageDetails)
    {
        log.debug("EvaluationServiceImpl.searchRegionEvaluationDetails - start | request: {}, pageDetails: {}", request, pageDetails);
        requireNonNull(request);
        requireNonNull(pageDetails);
        var page = countryEvaluationDetailsRepository
                .findAllRegionsWithCountriesAndEvaluatorsCount(
                        request.getSearch(),
                        pageDetails.toPageable());
        log.debug("EvaluationServiceImpl.searchRegionEvaluationDetails - end | request: {}, pageDetails: {}", request, pageDetails);
        return page;
    }

    public QuotationPackageDto saveQuotationPackage(QuotationPackageDto dto) {
        log.debug("EvaluationServiceImpl.saveQuotationPackageDto - start | dto: {}", dto);
        requireNonNull(dto);
        requireNotExists(dto);
        var quotationPackage = MAPPER.toEntity(dto);
        quotationPackageRepository.saveAndFlush(quotationPackage);
        var quotationPackageDto = MAPPER.toDto(quotationPackage);
        quotationPackageDto.setCountries(dto.getCountries());
        log.debug("EvaluationServiceImpl.saveQuotationPackageDto - start | dto: {}", dto);
        return quotationPackageDto;
    }

    @Override
    public Set<QuotationPackageCountryDto> saveQuotationPackageCountries(
            long packageId, Collection<QuotationPackageCountryDto> dtos)
    {
        log.debug("EvaluationServiceImpl.saveQuotationPackageCountries - start | packageId: {}, dtos: {}",
                packageId, dtos);
        requireNonNull(dtos);
        var quotationPackageCountryDtos = dtos
                .stream()
                .map(doto(this::requireNotExists))
                .map(doto(qpc -> qpc.setQuotationPackageId(packageId)))
                .map(MAPPER::toEntity)
                .map(quotationPackageCountryRepository::saveAndFlush)
                .map(MAPPER::toDto)
                .collect(toSet());
        log.debug("EvaluationServiceImpl.saveQuotationPackageCountries - end | packageId: {}, dtos: {}",
                packageId, dtos);
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
