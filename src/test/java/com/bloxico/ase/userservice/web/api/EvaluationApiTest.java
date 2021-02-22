package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilEvaluation;
import com.bloxico.ase.userservice.web.model.evaluation.PagedCountryEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.PagedRegionsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.web.api.EvaluationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_EXISTS;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.COUNTRY_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class EvaluationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilEvaluation utilEvaluation;

    @Test
    public void searchCountryEvaluationDetails_200_ok() {
        var search = genUUID();
        var c1 = utilEvaluation.savedCountryEvaluationDetailsCountedProj(genEmail(search));
        var c2 = utilEvaluation.savedCountryEvaluationDetailsCountedProj(genEmail(search.toUpperCase()));
        var c3 = utilEvaluation.savedCountryEvaluationDetailsCountedProj(genEmail(search.toLowerCase()));
        var c4 = utilEvaluation.savedCountryEvaluationDetailsCountedProj(genEmail("fooBar"));

        var countedProjs = given()
                .header("Authorization", utilAuth.doAuthentication())
                .queryParam("search", search)
                .when()
                .get(API_URL + EVALUATION_COUNTRY_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedCountryEvaluationDetailsResponse.class)
                .getCountryEvaluationDetails();

        assertThat(countedProjs, allOf(hasItems(c1, c2, c3), not(hasItems(c4))));
    }

    @Test
    public void saveCountryEvaluationDetails_404_countryNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(utilEvaluation.genSaveCountryEvaluationDetailsRequest(genUUID()))
                .when()
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_SAVE)
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
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_SAVE)
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
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_SAVE)
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
    public void updateCountryEvaluationDetails_404_evaluationDetailsNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", -1)
                .body(utilEvaluation.genUpdateCountryEvaluationDetailsRequest())
                .when()
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_EVALUATION_DETAILS_NOT_FOUND.getCode()));
    }

    @Test
    public void updateCountryEvaluationDetails_200_ok() {
        var details = utilEvaluation.savedCountryEvaluationDetails();
        var request = utilEvaluation.genUpdateCountryEvaluationDetailsRequest();
        var updatedDetails = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", details.getId())
                .body(request)
                .when()
                .post(API_URL + EVALUATION_COUNTRY_DETAILS_UPDATE)
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
    public void searchRegions_200_ok() {
        var search = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genEmail(search));
        var r2 = utilEvaluation.savedRegionCountedProj(genEmail(search.toUpperCase()));
        var r3 = utilEvaluation.savedRegionCountedProj(genEmail(search.toLowerCase()));
        var r4 = utilEvaluation.savedRegionCountedProj(genEmail("fooBar"));

        var countedProjs = given()
                .header("Authorization", utilAuth.doAuthentication())
                .queryParam("search", search)
                .when()
                .get(API_URL + EVALUATION_REGIONS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedRegionsResponse.class)
                .getRegions();

        assertThat(countedProjs, allOf(hasItems(r1, r2, r3), not(hasItems(r4))));
    }

}
