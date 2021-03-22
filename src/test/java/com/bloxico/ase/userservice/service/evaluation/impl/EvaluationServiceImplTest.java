package com.bloxico.ase.userservice.service.evaluation.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.exception.EvaluationException;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.SearchEvaluableArtworksRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.CATEGORY;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private UtilSecurityContext securityContext;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private EvaluationServiceImpl evaluationService;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Test
    public void findCountryEvaluationDetailsById_detailsNotFound() {
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.findCountryEvaluationDetailsById(-1L));
    }

    @Test
    public void findCountryEvaluationDetailsById() {
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        var foundDetails = evaluationService.findCountryEvaluationDetailsById(details.getId());
        assertEquals(foundDetails, details);
    }

    @Test
    public void countEvaluatorsByCountryId_countryNotFound() {
        assertEquals(0, evaluationService.countEvaluatorsByCountryId(-1L));
    }

    @Test
    public void countEvaluatorsByCountryId() {
        var c1 = utilLocation.savedCountry();
        assertEquals(0, evaluationService.countEvaluatorsByCountryId(c1.getId()));
        var c2 = utilUserProfile.savedEvaluator().getUserProfile().getLocation().getCountry();
        assertEquals(1, evaluationService.countEvaluatorsByCountryId(c2.getId()));
    }

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
    public void searchCountryEvaluationDetails_emptyRegions() {
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest(Collections.emptyList());
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchCountryEvaluationDetails(request, allPages()));
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
    public void searchCountryEvaluationDetails_withRegions() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var regionsFilter = List.of(region1.getName(), region2.getName());
        var request = utilEvaluation.genSearchCountryEvaluationDetailsRequest(regionsFilter);
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                allOf(hasItems(c1, c2), not(hasItems(c3))));
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
    public void searchCountryEvaluationDetails_forManagement_withRegions() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var regionsFilter = List.of(region1.getName(), region2.getName());
        var request = utilEvaluation.genSearchCountryEvaluationDetailsForManagementRequest(regionsFilter);
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        assertThat(
                evaluationService.searchCountryEvaluationDetails(request, allPages()),
                allOf(hasItems(c1, c2), not(hasItems(c3))));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails_nullDetails() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveCountryEvaluationDetails(null));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails_alreadyExists() {
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveCountryEvaluationDetails(details));
    }

    @Test
    @WithMockCustomUser
    public void saveCountryEvaluationDetails() {
        var countryId = utilLocation.savedCountry().getId();
        var details = utilEvaluation.genCountryEvaluationDetailsDto(countryId);
        var savedDetails = evaluationService.saveCountryEvaluationDetails(details);
        assertNotNull(savedDetails.getId());
        assertEquals(details.getCountryId(), savedDetails.getCountryId());
        assertEquals(details.getPricePerEvaluation(), savedDetails.getPricePerEvaluation());
        assertEquals(details.getAvailabilityPercentage(), savedDetails.getAvailabilityPercentage());
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails_nullDetails() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.updateCountryEvaluationDetails(null));
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails_evaluationDetailsNotFound() {
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        details.setId(-1L);
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveCountryEvaluationDetails(details));
    }

    @Test
    @WithMockCustomUser
    public void updateCountryEvaluationDetails() {
        var details = utilEvaluation.savedCountryEvaluationDetailsDto();
        var dto = utilEvaluation.genCountryEvaluationDetailsDto(details.getCountryId());
        dto.setId(details.getId());
        var updatedDetails = evaluationService.updateCountryEvaluationDetails(dto);
        assertEquals(details.getId(), updatedDetails.getId());
        assertEquals(details.getCountryId(), updatedDetails.getCountryId());
        assertEquals(dto.getPricePerEvaluation(), updatedDetails.getPricePerEvaluation());
        assertEquals(dto.getAvailabilityPercentage(), updatedDetails.getAvailabilityPercentage());
    }

    @Test
    public void deleteCountryEvaluationDetails_nullDetails() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.deleteCountryEvaluationDetails(null));
    }

    @Test
    public void deleteCountryEvaluationDetails() {
        var dto = utilEvaluation.savedCountryEvaluationDetailsDto();
        evaluationService.deleteCountryEvaluationDetails(dto);
        assertTrue(countryEvaluationDetailsRepository.findById(dto.getId()).isEmpty());
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
    @WithMockCustomUser
    public void saveQuotationPackage_nullPackage() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveQuotationPackage(null));
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackage_packageAlreadyExists() {
        var qPackage = utilEvaluation.savedQuotationPackageDto();
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveQuotationPackage(qPackage));
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackage() {
        var qPackageDto = utilEvaluation.genQuotationPackageDto();
        var savedQPackageDto = evaluationService.saveQuotationPackage(qPackageDto);
        assertNotNull(savedQPackageDto.getId());
        assertEquals(savedQPackageDto.getArtworkId(), qPackageDto.getArtworkId());
        assertSame(savedQPackageDto.getCountries(), qPackageDto.getCountries());
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackageCountries_nullPackageCountries() {
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.saveQuotationPackageCountries(packageId, null));
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackageCountries_packageCountryAlreadyExists() {
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        var qpc1 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc2 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc3 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        qpc2.setCountryId(qpc1.getCountryId());
        assertThrows(
                EvaluationException.class,
                () -> evaluationService.saveQuotationPackageCountries(
                        packageId, List.of(qpc1, qpc2, qpc3)));
    }

    @Test
    @WithMockCustomUser
    public void saveQuotationPackageCountries() {
        var packageId = utilEvaluation.savedQuotationPackage().getId();
        var qpc1 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc2 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qpc3 = utilEvaluation.genQuotationPackageCountryDto(packageId);
        var qPackageCountries = evaluationService
                .saveQuotationPackageCountries(
                        packageId, List.of(qpc1, qpc2, qpc3));
        for (var qpc : qPackageCountries) {
            assertNotNull(qpc.getCountryId());
            assertEquals(packageId, qpc.getQuotationPackageId());
            assertTrue(qpc.getNumberOfEvaluations() > 0);
        }
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_nullRequest() {
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchEvaluatedArtworks(null, allPages(), evaluatorId));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_nullPageRequest() {
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchEvaluatedArtworks(request, null, evaluatorId));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR)
    public void searchEvaluatedArtworks_ofEvaluator() {
        var evaluatorId = securityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj();
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest();
        assertThat(evaluationService
                        .searchEvaluatedArtworks(
                                request,
                                allPages(),
                                securityContext.getLoggedInUserId())
                        .getContent(),
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
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest(title);
        assertThat(evaluationService
                        .searchEvaluatedArtworks(
                                request,
                                allPages(),
                                securityContext.getLoggedInUserId())
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
        var request = utilEvaluation.genSearchEvaluatedArtworksRequest(cs);
        assertThat(evaluationService
                        .searchEvaluatedArtworks(
                                request,
                                allPages(),
                                securityContext.getLoggedInUserId())
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
        assertThat(evaluationService
                        .searchEvaluatedArtworks(request, allPages(), null)
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
        assertThat(evaluationService
                        .searchEvaluatedArtworks(request, allPages(), null)
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
        assertThat(evaluationService
                        .searchEvaluatedArtworks(request, allPages(), null)
                        .getContent(),
                allOf(hasItems(ea1, ea2, ea3), not(hasItems(ea4))));
    }

    @Test
    public void searchEvaluable_nullSearchRequest() {
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchEvaluableArtworks(null, allPages()));
    }

    @Test
    public void searchEvaluable_nullPageRequest() {
        var request = utilEvaluation.genSearchEvaluableArtworksRequest();
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchEvaluableArtworks(request, null));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluableByCountry() {
        long countryId = utilLocation.savedCountry().getId();
        var evaluable1 = utilEvaluation.savedOngoingEvaluationProj(countryId);
        var evaluable2 = utilEvaluation.savedOngoingEvaluationProj(countryId);
        var evaluable3 = utilEvaluation.savedOngoingEvaluationProj();
        var request = utilEvaluation.genSearchEvaluableArtworksRequest(countryId);
        assertThat(
                evaluationService.searchEvaluableArtworks(request, allPages()),
                allOf(hasItems(evaluable1, evaluable2), not(hasItems(evaluable3))));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluableByCountryAndTitle() {
        long countryId = utilLocation.savedCountry().getId();
        var evaluable1 = utilEvaluation.savedOngoingEvaluationProj(countryId);
        var evaluable2 = utilEvaluation.savedOngoingEvaluationProj(countryId);
        var evaluable3 = utilEvaluation.savedOngoingEvaluationProj();
        var request = utilEvaluation.genSearchEvaluableArtworksRequest(countryId, evaluable1.getArtworkTitle());
        assertThat(
                evaluationService.searchEvaluableArtworks(request, allPages()),
                allOf(hasItems(evaluable1), not(hasItems(evaluable2, evaluable3))));
    }

    @Test
    @WithMockCustomUser
    public void searchEvaluable() {
        var artwork = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.READY_FOR_EVALUATION));
        var categories = artwork.getCategories().stream().map(ArtworkMetadataDto::getName).collect(Collectors.toList());
        long countryId = utilLocation.savedCountry().getId();
        var evaluable1 = utilEvaluation.savedOngoingEvaluationProj(artwork, countryId);
        var evaluable2 = utilEvaluation.savedOngoingEvaluationProj(countryId);
        var evaluable3 = utilEvaluation.savedOngoingEvaluationProj();
        var request = new SearchEvaluableArtworksRequest(countryId, evaluable1.getArtworkTitle(), categories);
        assertThat(
                evaluationService.searchEvaluableArtworks(request, allPages()),
                allOf(hasItems(evaluable1), not(hasItems(evaluable2, evaluable3))));
    }

}
