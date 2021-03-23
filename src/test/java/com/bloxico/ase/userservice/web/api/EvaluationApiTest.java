package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.CATEGORY;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.entity.user.Role.USER;
import static com.bloxico.ase.userservice.web.api.EvaluationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.COUNTRY_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class EvaluationApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilSystem utilSystem;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Test
    @WithMockCustomUser(auth = true)
    public void searchCountryEvaluationDetails_200_ok() {
        var country = genUUID();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(genWithSubstring(country));
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genUUID());

        var content = given()
                .header("Authorization", utilSecurityContext.getToken())
                .params(allPages("search", country))
                .when()
                .get(API_URL + EVALUATION_COUNTRY_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCountryEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(c1, c2), not(hasItems(c3, c4))));
    }

    @Test
    @WithMockCustomUser(role = USER, auth = true)
    public void searchCountryEvaluationDetails_withRegions_200_ok() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);

        var content = given()
                .header("Authorization", utilSecurityContext.getToken())
                .params(allPages("search", ""))
                .param("regions", String.format("%s,%s", region1.getName(), region2.getName()))
                .when()
                .get(API_URL + EVALUATION_COUNTRY_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCountryEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(c1, c2), not(hasItems(c3, c4))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchCountryEvaluationDetailsForManagement_200_ok() {
        var country = genUUID();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(genWithSubstring(country));
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genUUID());

        var content = given()
                .header("Authorization", utilSecurityContext.getToken())
                .queryParams(allPages("search", country))
                .when()
                .get(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCountryEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(c1, c2, c3), not(hasItems(c4))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchCountryEvaluationDetailsForManagement_withRegions_200_ok() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);

        var content = given()
                .header("Authorization", utilSecurityContext.getToken())
                .params(allPages("search", ""))
                .param("regions", String.format("%s,%s", region1.getName(), region2.getName()))
                .when()
                .get(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCountryEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(c1, c2, c4), not(hasItems(c3))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveCountryEvaluationDetails_404_countryNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(utilEvaluation.genSaveCountryEvaluationDetailsRequest(genUUID()))
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveCountryEvaluationDetails_409_alreadyExists() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveCountryEvaluationDetails_200_ok() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        var details = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveCountryEvaluationDetailsResponse.class)
                .getCountryEvaluationDetails();
        assertNotNull(details.getId());
        assertNotNull(details.getCountryId());
        assertEquals(request.getPricePerEvaluation(), details.getPricePerEvaluation());
        assertEquals(request.getAvailabilityPercentage(), details.getAvailabilityPercentage());
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateCountryEvaluationDetails_404_detailsNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(utilEvaluation.genUpdateCountryEvaluationDetailsRequest(-1L))
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateCountryEvaluationDetails_200_ok() {
        var details = utilEvaluation.savedCountryEvaluationDetails();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(details.getId());
        var updatedDetails = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_UPDATE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UpdateCountryEvaluationDetailsResponse.class)
                .getCountryEvaluationDetails();
        assertEquals(details.getId(), updatedDetails.getId());
        assertEquals(details.getCountryId(), updatedDetails.getCountryId());
        assertEquals(request.getPricePerEvaluation(), updatedDetails.getPricePerEvaluation());
        assertEquals(request.getAvailabilityPercentage(), updatedDetails.getAvailabilityPercentage());
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteCountryEvaluationDetails_404_detailsNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", -1L)
                .when()
                .delete(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteCountryEvaluationDetails_409_countryHasEvaluators() {
        var evaluator = utilUserProfile.savedEvaluator();
        var countryId = evaluator.getUserProfile().getLocation().getCountry().getId();
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails(countryId).getId();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", evaluationDetailsId)
                .when()
                .delete(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteCountryEvaluationDetails_200_ok() {
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails().getId();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", evaluationDetailsId)
                .when()
                .delete(API_URL + MNG_EVALUATION_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        assertTrue(countryEvaluationDetailsRepository.findById(evaluationDetailsId).isEmpty());
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchRegionEvaluationDetailsForManagement_200_ok() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r2 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region.toUpperCase()));
        var r3 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region.toLowerCase()));
        var r4 = utilEvaluation.savedRegionCountedProj(genUUID());

        var content = given()
                .header("Authorization", utilSecurityContext.getToken())
                .queryParams(allPages("search", region))
                .when()
                .get(API_URL + MNG_EVALUATION_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(r1, r2, r3), not(hasItems(r4))));
    }

    // TODO test saveQuotationPackage_404_artworkNotFound()

    @Test
    @WithMockCustomUser(auth = true)
    public void saveQuotationPackage_409_packageAlreadyExists() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_QUOTATION_PACKAGE_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_QUOTATION_PACKAGE_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(QUOTATION_PACKAGE_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveQuotationPackage_200_ok() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        var qPackage = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_QUOTATION_PACKAGE_SAVE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveQuotationPackageResponse.class)
                .getQuotationPackage();
        assertNotNull(qPackage.getId());
        assertEquals(request.getArtworkId(), qPackage.getArtworkId());
        assertEquals(request.getCountries().size(), qPackage.getCountries().size());
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluatedArtworks() {
        var evaluator = utilSecurityContext.getLoggedInEvaluator();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(evaluator.getId());
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(evaluator.getId());
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj();
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("artworkTitle", ""))
                .when()
                .get(API_URL + EVALUATION_EVALUATED_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluatedArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluatedArtworks_withArtworkTitle() {
        var title = genUUID();
        var evaluatorId = utilSecurityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)), evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)), evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)));
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("artworkTitle", title))
                .when()
                .get(API_URL + EVALUATION_EVALUATED_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluatedArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluatedArtworks_withCategories() {
        var c1 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var c2 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var evaluatorId = utilSecurityContext.getLoggedInEvaluator().getId();
        var ea1 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1, c2)), evaluatorId);
        var ea2 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1)), evaluatorId);
        var ea3 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()), evaluatorId);
        var ea4 = utilEvaluation.savedEvaluatedArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c2)));
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("categories", String.format("%s,%s", c1.getName(), c2.getName())))
                .when()
                .get(API_URL + EVALUATION_EVALUATED_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluatedArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3, ea4))));
    }

    @Test
    @WithMockCustomUser(role = USER, auth = true)
    public void searchEvaluableArtworks_notAuthorized() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("countryId", 1L))
                .when()
                .get(API_URL + SEARCH_EVALUABLE_ARTWORKS)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluableArtworks_byCountry() {
        var countryId = utilLocation.savedCountry().getId();
        var ea1 = utilEvaluation.savedEvaluableArtworkProj(countryId);
        var ea2 = utilEvaluation.savedEvaluableArtworkProj(countryId);
        var ea3 = utilEvaluation.savedEvaluableArtworkProj();
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages("countryId", countryId))
                .when()
                .get(API_URL + SEARCH_EVALUABLE_ARTWORKS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluableArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluableArtworks_byCountryAndTitle() {
        var title = genUUID();
        var countryId = utilLocation.savedCountry().getId();
        var ea1 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)),
                countryId);
        var ea2 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genWithSubstring(title)),
                countryId);
        var ea3 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()),
                countryId);
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages(Map.of("countryId", countryId, "title", title)))
                .when()
                .get(API_URL + SEARCH_EVALUABLE_ARTWORKS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluableArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3))));
    }

    @Test
    @WithMockCustomUser(role = EVALUATOR, auth = true)
    public void searchEvaluableArtworks_byCountryAndCategories() {
        long countryId = utilLocation.savedCountry().getId();
        var c1 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var c2 = utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, APPROVED);
        var ea1 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1, c2)), countryId);
        var ea2 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c1)), countryId);
        var ea3 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(Set.of(c2)), countryId);
        var ea4 = utilEvaluation.savedEvaluableArtworkProj(
                utilArtwork.savedEvaluableArtworkDto(genUUID()), countryId);
        var response = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .params(allPages(Map.of(
                        "countryId", countryId,
                        "categories", String.format("%s", c1.getName()))))
                .when()
                .get(API_URL + SEARCH_EVALUABLE_ARTWORKS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchEvaluableArtworksResponse.class);
        assertThat(
                response.getPage().getContent(),
                allOf(hasItems(ea1, ea2), not(hasItems(ea3, ea4))));
    }

}