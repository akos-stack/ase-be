package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.EvaluationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private EvaluationServiceImpl evaluationService;

    @Test
    public void findAllCountryEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.findAllCountryEvaluationDetails(null)
        );
    }

    @Test
    public void findAllCountryEvaluationDetails() {
        var request = utilEvaluation.genDefaultSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationService.findAllCountryEvaluationDetails(request), hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationService.findAllCountryEvaluationDetails(request), hasItems(c1, c2));
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

}
