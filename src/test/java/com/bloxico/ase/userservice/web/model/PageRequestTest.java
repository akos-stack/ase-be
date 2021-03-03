package com.bloxico.ase.userservice.web.model;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilEvaluation;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.userservice.facade.impl.EvaluationFacadeImpl;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsRequest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.EvaluationApi.EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
public class PageRequestTest extends AbstractSpringTest {

    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilAuth utilAuth;
    @Autowired private EvaluationFacadeImpl evaluationFacadeImpl;
    @Autowired private UtilEvaluation utilEvaluation;

    @Test
    public void searchRegionEvaluationDetailsForManagement_200_ok() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType("application/json")
                .formParams(utilEvaluation.genRegionEvaluationDetailsFormParams(0, 10, "name", "DESC", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_400_page_min_value() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType("application/json")
                .formParams(utilEvaluation.genRegionEvaluationDetailsFormParams(-1, 10, "name", "DESC", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_400_size_min_value() {
        RestAssured.registerParser("text/plain", Parser.JSON);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType("application/json")
                .formParams(utilEvaluation.genRegionEvaluationDetailsFormParams(0, 0, "name", "DESC", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement() {
        var r1 = utilLocation.savedRegion("a");
        var r2 = utilLocation.savedRegion("b");
        var r3 = utilLocation.savedRegion("c");
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 10, "name", "DESC");
        var response = evaluationFacadeImpl.searchRegionEvaluationDetails(request, pageDetails);
        assertNotNull(response.getPage().getContent());
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_test_min_page_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(-1, 10, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacadeImpl.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_test_min_size_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 0, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacadeImpl.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_null_page_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(null, 10, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacadeImpl.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_null_size_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, null, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacadeImpl.searchRegionEvaluationDetails(request, pageDetails));
    }
}
