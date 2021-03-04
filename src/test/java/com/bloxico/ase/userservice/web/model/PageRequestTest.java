package com.bloxico.ase.userservice.web.model;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilEvaluation;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.userservice.facade.impl.EvaluationFacadeImpl;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.service.evaluation.impl.EvaluationServiceImpl;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsRequest;
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
    @Autowired private EvaluationFacadeImpl evaluationFacade;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private EvaluationServiceImpl evaluationService;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void searchRegionEvaluationDetails_200_ok() {
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
    public void searchRegionEvaluationDetails_400_page_min_value() {
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
    public void searchRegionEvaluationDetails_400_size_min_value() {
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
    public void searchRegionEvaluationDetails() {
        var r1 = utilLocation.savedRegion("a");
        var r2 = utilLocation.savedRegion("b");
        var r3 = utilLocation.savedRegion("c");
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 10, "name", "DESC");
        var response = evaluationFacade.searchRegionEvaluationDetails(request, pageDetails);
        var foundRegion =  response.getPage().getContent().get(0);
        assertEquals(foundRegion.getName(), r2.getName());
        assertNotNull(response.getPage().getContent());
    }

    @Test
    public void searchRegionEvaluationDetails_test_min_page_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(-1, 10, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_test_min_size_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 0, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_null_page_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(null, 10, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_null_size_value() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, null, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_testService() {
        var r1 = utilLocation.savedRegion("a");
        var r2 = utilLocation.savedRegion("b");
        var r3 = utilLocation.savedRegion("c");
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 10, "name", "DESC");
        var foundRegions = evaluationService.searchRegionEvaluationDetails(request, pageDetails);
        var foundRegion =  foundRegions.getContent().get(0);
        assertEquals(foundRegion.getName(), r2.getName());
        assertNotNull(foundRegions.getContent());
    }

    @Test
    public void searchRegionEvaluationDetails_testService_null_page_param() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(null, 10, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_testService_null_size_param() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, null, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_testService_min_size_param() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 0, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationService.searchRegionEvaluationDetails(request, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetails_testService_min_page_param() {
        var request = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(-1, 10, "name", "DESC");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationService.searchRegionEvaluationDetails(request, pageDetails));
    }
}
