package com.bloxico.ase.userservice.service.quotationpackage.impl;

import com.bloxico.ase.userservice.dto.entity.quotationpackage.QuotationPackageDto;
import com.bloxico.ase.userservice.entity.quotationpackage.QuotationPackage;
import com.bloxico.ase.userservice.repository.quotationpackage.QuotationPackageRepository;
import com.bloxico.ase.userservice.service.quotationpackage.IQuotationPackageService;
import com.bloxico.ase.userservice.web.model.quotationpackage.CreateQuotationPackageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.QuotationPackage.QUOTATION_PACKAGE_EXISTS;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class QuotationPackageServiceImpl implements IQuotationPackageService {

    private final QuotationPackageRepository quotationPackageRepository;

    @Autowired
    public QuotationPackageServiceImpl(
            QuotationPackageRepository quotationPackageRepository) {

        this.quotationPackageRepository = quotationPackageRepository;
    }

    @Override
    public List<QuotationPackageDto> findAll() {

        log.debug("QuotationPackageServiceImpl.findAll - start");

        var quotationPackages = getQuotationPackages();

        log.debug("QuotationPackageServiceImpl.findAll - end");
        return quotationPackages;
    }

    @Override
    public QuotationPackageDto createQuotationPackage(
            CreateQuotationPackageRequest request, long principalId) {

        log.debug("QuotationPackageServiceImpl.createQuotationPackage - start | request: {}, principalId: {}",
                request, principalId);

        requireNonNull(request);

        if (isQuotationPackageNameTaken(request.getName())) {
            throw QUOTATION_PACKAGE_EXISTS.newException();
        }

        var quotationPackage = MAPPER.toQuotationPackage(request);
        quotationPackage.setCreatorId(principalId);
        quotationPackageRepository.saveAndFlush(quotationPackage);

        log.debug("QuotationPackageServiceImpl.createQuotationPackage - end | request: {}, principalId: {}",
                request, principalId);
        return MAPPER.toDto(quotationPackage);
    }

    private boolean isQuotationPackageNameTaken(String name) {
        return quotationPackageRepository
                .findByNameIgnoreCase(name)
                .isPresent();
    }

    private List<QuotationPackageDto> getQuotationPackages() {
        return quotationPackageRepository
                .findAll()
                .stream()
                // This wasn't specified but on every site I've seen so far packages are sorted by price
                // Sorting can be also done on the client since pagination is not applied (for now)
                .sorted(comparing(QuotationPackage::getPrice))
                .map(MAPPER::toDto)
                .collect(Collectors.toList());
    }

}
