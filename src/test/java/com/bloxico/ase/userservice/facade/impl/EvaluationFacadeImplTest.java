package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.EvaluationException;
import com.bloxico.ase.userservice.exception.LocationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvaluationFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private EvaluationFacadeImpl evaluationFacade;

    @Test
    public void searchCountriesWithEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchCountriesWithEvaluationDetails(null)
        );
    }

    @Test
    public void searchCountriesWithEvaluationDetails() {
        var request = utilEvaluation.genDefaultSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationFacade.searchCountriesWithEvaluationDetails(request).getCountryEvaluationDetails(), hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationFacade.searchCountriesWithEvaluationDetails(request).getCountryEvaluationDetails(),
                allOf(hasItems(c1), not(hasItems(c2))));
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
    public void searchCountries_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchCountries(null)
        );
    }

    @Test
    public void searchCountries() {
        var request = utilEvaluation.genDefaultSearchCountryEvaluationDetailsRequest();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(evaluationFacade.searchCountries(request).getCountryEvaluationDetails(), hasItems(c1));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetails();
        assertThat(evaluationFacade.searchCountries(request).getCountryEvaluationDetails(), hasItems(c1, c2));
    }

    @Test
    public void searchRegions_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegions(null)
        );
    }

    @Test
    public void searchRegions() {
        var request = utilEvaluation.genDefaultSearchRegionsRequest();
        var c1 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationFacade.searchRegions(request).getRegions(), hasItems(c1));
        var c2 = utilEvaluation.savedRegionCountedProj();
        assertThat(evaluationFacade.searchRegions(request).getRegions(), hasItems(c1, c2));
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
