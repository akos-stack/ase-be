package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.EvaluationException;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvaluationFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private UtilUser utilUser;
    @Autowired
    private UtilLocation utilLocation;
    @Autowired
    private UtilEvaluation utilEvaluation;
    @Autowired
    private UtilUserProfile utilUserProfile;
    @Autowired
    private EvaluationFacadeImpl evaluationFacade;
    @Autowired
    private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Test
    public void searchCountryEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchCountryEvaluationDetails(null, allPages()));
    }

    @Test
    public void searchCountryEvaluationDetails_nullPageRequest() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchCountryEvaluationDetails(request, null));
    }

    @Test
    public void searchCountryEvaluationDetails_emptyRegions() {
        var request = utilEvaluation
                .genSearchCountryEvaluationDetailsRequest(Collections.emptyList());
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchCountryEvaluationDetails(request, allPages()));
    }

    @Test
    public void searchCountryEvaluationDetails() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationFacade
                        .searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationFacade
                        .searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                allOf(hasItems(c1), not(hasItems(c2))));
    }

    @Test
    public void searchCountryEvaluationDetails_withRegions() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var regionsFilter = Arrays.asList(region1.getName(), region2.getName());
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest(regionsFilter);
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationFacade.searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                allOf(hasItems(c1, c2), not(hasItems(c3))));
    }

    @Test
    public void searchCountryEvaluationDetails_forManagement() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsForManagementRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationFacade
                        .searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationFacade
                        .searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                hasItems(c1, c2));
    }

    @Test
    public void searchCountryEvaluationDetails_forManagement_withRegions() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var regionsFilter = Arrays.asList(region1.getName(), region2.getName());
        var request = utilEvaluation.genSearchCountryEvaluationDetailsForManagementRequest(regionsFilter);
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationFacade.searchCountryEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                allOf(hasItems(c1, c2), not(hasItems(c3))));
    }

    @Test
    public void saveCountryEvaluationDetails_nullRequest() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(null, principalId));
    }

    @Test
    public void saveCountryEvaluationDetails_countryNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest(genUUID());
        assertThrows(
                LocationException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(request, principalId));
    }

    @Test
    public void saveCountryEvaluationDetails_detailsAlreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var r1 = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        evaluationFacade.saveCountryEvaluationDetails(r1, principalId);
        var r2 = utilEvaluation.genSaveCountryEvaluationDetailsRequest(r1.getCountry());
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(r2, principalId));
    }

    @Test
    public void saveCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        evaluationFacade.saveCountryEvaluationDetails(request, principalId);
    }

    @Test
    public void updateCountryEvaluationDetails_nullRequest() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.updateCountryEvaluationDetails(null, principalId));
    }

    @Test
    public void updateCountryEvaluationDetails_evaluationDetailsNotFound() {
        var principalId = utilUser.savedAdmin().getId();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(-1);
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.updateCountryEvaluationDetails(request, principalId));
    }

    @Test
    public void updateCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var details = utilEvaluation.savedCountryEvaluationDetails();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(details.getId());
        var updatedDetails = evaluationFacade
                .updateCountryEvaluationDetails(request, principalId)
                .getCountryEvaluationDetails();
        assertEquals(details.getId(), updatedDetails.getId());
        assertEquals(details.getCountryId(), updatedDetails.getCountryId());
        assertEquals(request.getPricePerEvaluation(), updatedDetails.getPricePerEvaluation());
        assertEquals(request.getAvailabilityPercentage(), updatedDetails.getAvailabilityPercentage());
    }

    @Test
    public void deleteCountryEvaluationDetails_detailsNotFound() {
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.deleteCountryEvaluationDetails(-1));
    }

    @Test
    public void deleteCountryEvaluationDetails_countryHasEvaluators() {
        var evaluator = utilUserProfile.savedEvaluator();
        var countryId = evaluator.getUserProfile().getLocation().getCountry().getId();
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails(countryId).getId();
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.deleteCountryEvaluationDetails(evaluationDetailsId));
    }

    @Test
    public void deleteCountryEvaluationDetails() {
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails().getId();
        evaluationFacade.deleteCountryEvaluationDetails(evaluationDetailsId);
        assertTrue(countryEvaluationDetailsRepository.findById(evaluationDetailsId).isEmpty());
    }

    @Test
    public void searchRegionEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(null, allPages()));
    }

    @Test
    public void searchRegionEvaluationDetails_nullPageRequest() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(request, null));
    }

    @Test
    public void searchRegionEvaluationDetails() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        var c1 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationFacade
                        .searchRegionEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                hasItems(c1));
        var c2 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationFacade
                        .searchRegionEvaluationDetails(request, allPages())
                        .getPage()
                        .getContent(),
                hasItems(c1, c2));
    }

    @Test
    public void saveQuotationPackage_nullRequest() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.saveQuotationPackage(null, principalId));
    }

    // TODO test saveQuotationPackage_artworkNotFound

    @Test
    public void saveQuotationPackage_packageAlreadyExists() {
        var principalId = utilUser.savedAdmin().getId();
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        evaluationFacade.saveQuotationPackage(request, principalId);
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.saveQuotationPackage(request, principalId));
    }

    @Test
    public void saveQuotationPackage() {
        var principalId = utilUser.savedAdmin().getId();
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        evaluationFacade.saveQuotationPackage(request, principalId);
    }

}
