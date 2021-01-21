package com.bloxico.ase.userservice.service.quotationpackage.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.QuotationPackageException;
import com.bloxico.ase.userservice.repository.quotationpackage.QuotationPackageRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuotationPackageServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private QuotationPackageServiceImpl quotationPackageService;

    @Autowired
    private QuotationPackageRepository quotationPackageRepository;

    @Test
    public void findAll_emptyResultSet() {
        var list = quotationPackageService
                .findAll();

        assertEquals(0, list.size());
    }

    @Test
    public void findAll_quotationPackagesSuccessfullyRetrieved() {
        var names = Arrays.asList("Basic", "Advanced", "Pro");
        var expectedList = mockUtil
                .savedQuotationPackageDtos(names);

        var actualList = quotationPackageService
                .findAll();

        assertEquals(expectedList, actualList);
    }

    @Test(expected = NullPointerException.class)
    public void createQuotationPackage_requestIsNull() {
        quotationPackageService
                .createQuotationPackage(null, mockUtil.savedAdmin().getId());
    }

    @Test(expected = QuotationPackageException.class)
    public void createQuotationPackage_quotationPackageAlreadyExists() {
        var adminId = mockUtil.savedAdmin().getId();

        var request1 =
                mockUtil.doCreateQuotationPackageRequest("Basic");
        quotationPackageService.createQuotationPackage(request1, adminId);

        var request2 =
                mockUtil.doCreateQuotationPackageRequest("Basic");
        quotationPackageService.createQuotationPackage(request2, adminId);
    }

    @Test
    public void createQuotationPackage_quotationPackageSuccessfullyCreated() {
        var adminId = mockUtil.savedAdmin().getId();

        var request =
                mockUtil.doCreateQuotationPackageRequest("Basic");
        quotationPackageService.createQuotationPackage(request, adminId);

        var newlyCreatedQuotationPackage = quotationPackageRepository
                        .findByNameIgnoreCase(request.getName())
                        .orElse(null);

        assertNotNull(newlyCreatedQuotationPackage);
        assertNotNull(newlyCreatedQuotationPackage.getId());
        assertEquals(adminId, newlyCreatedQuotationPackage.getCreatorId());
        assertEquals(request.getName(), newlyCreatedQuotationPackage.getName());
        assertEquals(request.getDescription(), newlyCreatedQuotationPackage.getDescription());
        assertEquals(request.getImagePath(), newlyCreatedQuotationPackage.getImagePath());
        assertEquals(request.getPrice(), newlyCreatedQuotationPackage.getPrice());
        assertEquals(request.getNumberOfEvaluations().intValue(), newlyCreatedQuotationPackage.getNumberOfEvaluations());
        assertEquals(request.getActive(), newlyCreatedQuotationPackage.isActive());
    }

}
