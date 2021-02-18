package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.EvaluationApi.EVALUATION_COUNTRY_DETAILS_SAVE;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Evaluation.COUNTRY_EVALUATION_DETAILS_EXISTS;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.COUNTRY_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class EvaluationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilEvaluation utilEvaluation;

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

}
