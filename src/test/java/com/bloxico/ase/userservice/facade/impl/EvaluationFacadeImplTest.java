package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.exception.EvaluationException;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.CATEGORY;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvaluationFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilSecurityContext securityContext;
    @Autowired private UtilSystem utilSystem;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private EvaluationFacadeImpl evaluationFacade;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

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
        var regionsFilter = List.of(region1.getName(), region2.getName());
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
        var regionsFilter = List.of(region1.getName(), region2.getName());
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
    @WithMockCustomUser
    public void saveCountryEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(null));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails_countryNotFound() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest(genUUID());
        assertThrows(
                LocationException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(request));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails_detailsAlreadyExists() {
        var r1 = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        evaluationFacade.saveCountryEvaluationDetails(r1);
        var r2 = utilEvaluation.genSaveCountryEvaluationDetailsRequest(r1.getCountry());
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.saveCountryEvaluationDetails(r2));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        evaluationFacade.saveCountryEvaluationDetails(request);
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.updateCountryEvaluationDetails(null));
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails_evaluationDetailsNotFound() {
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(-1L);
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.updateCountryEvaluationDetails(request));
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails() {
        var details = utilEvaluation.savedCountryEvaluationDetails();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(details.getId());
        var updatedDetails = evaluationFacade
                .updateCountryEvaluationDetails(request)
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
                () -> evaluationFacade.deleteCountryEvaluationDetails(-1L));
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
    @WithMockCustomUser
    public void saveQuotationPackage_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.saveQuotationPackage(null));
    }

    // TODO test saveQuotationPackage_artworkNotFound

    @Test
    @WithMockCustomUser
    public void saveQuotationPackage_packageAlreadyExists() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        evaluationFacade.saveQuotationPackage(request);
        assertThrows(
                EvaluationException.class,
                () -> evaluationFacade.saveQuotationPackage(request));
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackage() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        evaluationFacade.saveQuotationPackage(request);
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluatedArtworks_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchEvaluatedArtworks(null, allPages(), 1L));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluatedArtworks_nullPageRequest() {
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchEvaluatedArtworks(request, null, 1L));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_ofEvaluator() {
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj();
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(
                                utilEvaluation.genSearchEvaluatedArtworksRequest(),
                                allPages(),
                                securityContext.getLoggedInUserId())
                        .getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_ofEvaluator_withArtworkTitle() {
        var title = genUUID();
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)),
                evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()),
                evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)));
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(
                                utilEvaluation.genSearchEvaluatedArtworksRequest(title),
                                allPages(),
                                securityContext.getLoggedInUserId())
                        .getPage()
                        .getContent(),
                allOf(hasItems(ea1), not(hasItems(ea2, ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_ofEvaluator_withCategories() {
        var c1 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var c2 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var cs = List.of(c1.getName(), c2.getName());
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1, c2)),
                evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1)),
                evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()),
                evaluatorId);
        var ea4 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c2)));
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(
                                utilEvaluation.genSearchEvaluatedArtworksRequest(cs),
                                allPages(),
                                securityContext.getLoggedInUserId())
                        .getPage()
                        .getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3, ea4))));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluatedArtworks_all() {
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj();
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj();
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj();
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest();
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(request, allPages(), null)
                        .getPage()
                        .getContent(),
                hasItems(ea1, ea2, ea3));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluatedArtworks_all_withArtworkTitle() {
        var title = genUUID();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)));
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)));
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()));
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest(title);
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(request, allPages(), null)
                        .getPage()
                        .getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluatedArtworks_all_withCategories() {
        var c1 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var c2 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var cs = List.of(c1.getName(), c2.getName());
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1, c2)));
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1)));
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c2)));
        var ea4 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()));
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest(cs);
        assertThat(evaluationFacade
                        .searchEvaluatedArtworks(request, allPages(), null)
                        .getPage()
                        .getContent(),
                allOf(hasItems(ea1, ea2, ea3), not(hasItems(ea4))));
    }

}
