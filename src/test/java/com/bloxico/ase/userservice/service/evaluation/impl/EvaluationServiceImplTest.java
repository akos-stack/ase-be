package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.EvaluationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.Util.allPages;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private EvaluationServiceImpl evaluationService;

    @Test
    public void searchCountryEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchCountryEvaluationDetails(null, allPages()));
    }

    @Test
    public void searchCountryEvaluationDetails_nullPageRequest() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchCountryEvaluationDetails(request, null));
    }

    @Test
    public void searchCountryEvaluationDetails() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                allOf(hasItems(c1), not(hasItems(c2))));
    }

    @Test
    public void searchCountryEvaluationDetails_forManagement() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsForManagementRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                hasItems(c1, c2));
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
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
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
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        details.setId(-1L);
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveCountryEvaluationDetails(details, principalId));
    }

    @Test
    public void updateCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        var dto = utilEvaluation.genCountryEvaluationDetailsDto(details.getCountryId());
        dto.setId(details.getId());
        var updatedDetails = evaluationService.updateCountryEvaluationDetails(dto, principalId);
        assertEquals(details.getId(), updatedDetails.getId());
        assertEquals(details.getCountryId(), updatedDetails.getCountryId());
        assertEquals(dto.getPricePerEvaluation(), updatedDetails.getPricePerEvaluation());
        assertEquals(dto.getAvailabilityPercentage(), updatedDetails.getAvailabilityPercentage());
    }

    @Test
    public void searchRegionEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(null, allPages()));
    }

    @Test
    public void searchRegionEvaluationDetails_nullPageRequest() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(request, null));
    }

    @Test
    public void searchRegionEvaluationDetails() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        var c1 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationService
                        .searchRegionEvaluationDetails(request, allPages()),
                hasItems(c1));
        var c2 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationService
                        .searchRegionEvaluationDetails(request, allPages()),
                hasItems(c1, c2));
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
