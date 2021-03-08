package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
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

    @Autowired
    private UtilAuth utilAuth;
    @Autowired
    private UtilEvaluation utilEvaluation;
    @Autowired
    private UtilLocation utilLocation;
    @Autowired
    private UtilUserProfile utilUserProfile;
    @Autowired
    private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    @Test
    public void searchCountryEvaluationDetails_200_ok() {
        var country = genUUID();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(genWithSubstring(country));
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genUUID());

        var content = given()
                .header("Authorization", utilAuth.doAuthentication())
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
    public void searchCountryEvaluationDetails_withRegions_200_ok() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);

        var content = given()
                .header("Authorization", utilAuth.doAuthentication())
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
    public void searchCountryEvaluationDetailsForManagement_200_ok() {
        var country = genUUID();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genWithSubstring(country));
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(genWithSubstring(country));
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithCountryName(genUUID());

        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParams(allPages("search", country))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SEARCH)
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
    public void searchCountryEvaluationDetailsForManagement_withRegions_200_ok() {
        var region1 = utilLocation.savedRegion();
        var region2 = utilLocation.savedRegion();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region1);
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProjWithRegion(region2);
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj();
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(region2);

        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .params(allPages("search", ""))
                .param("regions", String.format("%s,%s", region1.getName(), region2.getName()))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SEARCH)
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
    public void saveCountryEvaluationDetails_404_countryNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(utilEvaluation.genSaveCountryEvaluationDetailsRequest(genUUID()))
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    public void saveCountryEvaluationDetails_409_alreadyExists() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_EXISTS.getCode()));
    }

    @Test
    public void saveCountryEvaluationDetails_200_ok() {
        var request = utilEvaluation.genSaveCountryEvaluationDetailsRequest();
        var details = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_SAVE)
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
    public void updateCountryEvaluationDetails_404_detailsNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(utilEvaluation.genUpdateCountryEvaluationDetailsRequest(-1))
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_NOT_FOUND.getCode()));
    }

    @Test
    public void updateCountryEvaluationDetails_200_ok() {
        var details = utilEvaluation.savedCountryEvaluationDetails();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest(details.getId());
        var updatedDetails = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_UPDATE)
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
    public void deleteCountryEvaluationDetails_404_detailsNotFound() {
        var request = new DeleteCountryEvaluationDetailsRequest(-1);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_NOT_FOUND.getCode()));
    }

    @Test
    public void deleteCountryEvaluationDetails_409_countryHasEvaluators() {
        var evaluator = utilUserProfile.savedEvaluator();
        var countryId = evaluator.getUserProfile().getLocation().getCountry().getId();
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails(countryId).getId();
        var request = new DeleteCountryEvaluationDetailsRequest(evaluationDetailsId);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED.getCode()));
    }

    @Test
    public void deleteCountryEvaluationDetails_200_ok() {
        var evaluationDetailsId = utilEvaluation.savedCountryEvaluationDetails().getId();
        var request = new DeleteCountryEvaluationDetailsRequest(evaluationDetailsId);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_MANAGEMENT_COUNTRY_DETAILS_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        assertTrue(countryEvaluationDetailsRepository.findById(evaluationDetailsId).isEmpty());
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_200_ok() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r2 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region.toUpperCase()));
        var r3 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region.toLowerCase()));
        var r4 = utilEvaluation.savedRegionCountedProj(genUUID());

        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParams(allPages("search", region))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();

        assertThat(content, allOf(hasItems(r1, r2, r3), not(hasItems(r4))));
    }

    // TODO test saveQuotationPackage_404_artworkNotFound()

    @Test
    public void saveQuotationPackage_409_packageAlreadyExists() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_QUOTATION_PACKAGE_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
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
    public void saveQuotationPackage_200_ok() {
        var request = utilEvaluation.genSaveQuotationPackageRequest();
        var qPackage = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
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

}