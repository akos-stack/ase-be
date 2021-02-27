package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilEvaluation;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.exception.EvaluationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private UtilUser utilUser;
    @Autowired
    private UtilLocation utilLocation;
    @Autowired
    private UtilEvaluation utilEvaluation;
    @Autowired
    private EvaluationServiceImpl evaluationService;

    @Test
    public void findAllCountriesWithEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.findAllCountriesWithEvaluationDetails(null)
        );
    }

    @Test
    public void findAllCountriesWithEvaluationDetails() {
        var request = utilEvaluation.genDefaultSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationService.findAllCountriesWithEvaluationDetails(request), hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationService.findAllCountriesWithEvaluationDetails(request), allOf(hasItems(c1), not(hasItems(c2))));
    }

    @Test
    public void saveCountryEvaluationDetails_nullDetails() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveCountryEvaluationDetails(null, principalId));
    }

    @Test
    public void saveCountryEvaluationDetails_alreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilEvaluation.savedCountryEvaluationDetailsDto().getCountryId();
        var details = utilEvaluation.genCountryEvaluationDetailsDto(countryId);
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveCountryEvaluationDetails(details, principalId));
    }

    @Test
    public void saveCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountry().getId();
        var details = utilEvaluation.genCountryEvaluationDetailsDto(countryId);
        var savedDetails = evaluationService.saveCountryEvaluationDetails(details, principalId);
        assertNotNull(savedDetails.getId());
        assertEquals(details.getCountryId(), savedDetails.getCountryId());
        assertEquals(details.getPricePerEvaluation(), savedDetails.getPricePerEvaluation());
        assertEquals(details.getAvailabilityPercentage(), savedDetails.getAvailabilityPercentage());
    }

    @Test
    public void updateCountryEvaluationDetails_nullDetails() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.updateCountryEvaluationDetails(null, principalId));
    }

    @Test
    public void updateCountryEvaluationDetails_evaluationDetailsNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilEvaluation.savedCountryEvaluationDetailsDto().getCountryId();
        var details = utilEvaluation.genCountryEvaluationDetailsDto(countryId);
        details.setId(-1);
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveCountryEvaluationDetails(details, principalId));
    }

    @Test
    public void updateCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        var dto = utilEvaluation.genCountryEvaluationDetailsDto(details.getCountryId(),
                15, 40);
        dto.setId(details.getId());
        var updatedDetails = evaluationService.updateCountryEvaluationDetails(dto, principalId);
        assertEquals(details.getId(), updatedDetails.getId());
        assertEquals(details.getCountryId(), updatedDetails.getCountryId());
        assertEquals(15, updatedDetails.getPricePerEvaluation().intValue());
        assertEquals(40, updatedDetails.getAvailabilityPercentage().intValue());
    }

    @Test
    public void findAllCountries_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.findAllCountries(null)
        );
    }

    @Test
    public void findAllCountries() {
        var request = utilEvaluation.genDefaultSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationService.findAllCountries(request), hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationService.findAllCountries(request), hasItems(c1, c2));
    }

    @Test
    public void findAllRegions_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.findAllRegions(null)
        );
    }

    @Test
    public void findAllRegions() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        var c1 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationService.findAllRegions(request), hasItems(c1));
        var c2 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationService.findAllRegions(request), hasItems(c1, c2));
    }

    @Test
    public void saveQuotationPackage_nullPackage() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveQuotationPackage(null, principalId));
    }

    @Test
    public void saveQuotationPackage_packageAlreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var qPackage = utilEvaluation.savedQuotationPackageDto();
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveQuotationPackage(qPackage, principalId));
    }

    @Test
    public void saveQuotationPackage() {
        var principalId = utilUser.savedAdmin().getId();
        var qPackageDto = utilEvaluation.genQuotationPackageDto();
        var savedQPackageDto = evaluationService.saveQuotationPackage(qPackageDto, principalId);
        assertNotNull(savedQPackageDto.getId());
        assertEquals(savedQPackageDto.getArtworkId(), qPackageDto.getArtworkId());
        assertSame(savedQPackageDto.getCountries(), qPackageDto.getCountries());
    }

    @Test
    public void saveQuotationPackageCountries_nullPackageCountries() {
        var principalId = utilUser.savedAdmin().getId();
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveQuotationPackageCountries(packageId, null, principalId));
    }

    @Test
    public void saveQuotationPackageCountries_packageCountryAlreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        var qpc1 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc2 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc3 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        qpc2.setCountryId(qpc1.getCountryId());
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveQuotationPackageCountries(
                        packageId, List.of(qpc1, qpc2, qpc3), principalId));
    }

    @Test
    public void saveQuotationPackageCountries() {
        var principalId = utilUser.savedAdmin().getId();
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        var qpc1 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc2 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc3 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qPackageCountries = evaluationService
                .saveQuotationPackageCountries(
                        packageId, List.of(qpc1, qpc2, qpc3), principalId);
        for (var qpc : qPackageCountries) {
            assertNotNull(qpc.getCountryId());
            assertEquals(packageId, qpc.getQuotationPackageId());
            assertTrue(qpc.getNumberOfEvaluations() > 0);
        }
    }

}
