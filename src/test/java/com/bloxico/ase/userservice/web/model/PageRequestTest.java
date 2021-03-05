package com.bloxico.ase.userservice.web.model;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.facade.impl.EvaluationFacadeImpl;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.service.evaluation.impl.EvaluationServiceImpl;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.bloxico.ase.userservice.web.api.EvaluationApi.EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
    public void searchRegionEvaluationDetailsForManagement_200_ok() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("a");
        var r3 = utilEvaluation.savedRegionCountedProj("a");
        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(0, Integer.MAX_VALUE, "name", "desc", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result = content.stream().findAny();
        assertTrue(result.isPresent());
        assertThat(content, hasItems(r1, r2, r3));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_200_search() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("b".toUpperCase());
        var r3 = utilEvaluation.savedRegionCountedProj("c".toLowerCase());
        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(0, Integer.MAX_VALUE, "name", "asc", "b"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result = content.stream().findAny();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result_find_first = content.stream().findFirst();
        assertTrue(result.isPresent());
        assertThat(result_find_first.get().getName(), is(r2.getName()));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_200_order_desc() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("m");
        var r3 = utilEvaluation.savedRegionCountedProj("z");
        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(0, Integer.MAX_VALUE, "name", "desc", ""))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result = content.stream().findAny();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result_find_first = content.stream().findFirst();
        assertTrue(result.isPresent());
        assertThat(result_find_first.get().getName(), is(r3.getName()));
        assertThat(result.get(), anyOf(is(r1), is(r2), is(r3)));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_200_order_asc() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("m");
        var r3 = utilEvaluation.savedRegionCountedProj("z");
        var content = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(0, Integer.MAX_VALUE, "name", "asc", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage()
                .getContent();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result = content.stream().findAny();
        Optional<RegionWithCountriesAndEvaluatorsCountProj> result_find_first = content.stream().findFirst();
        assertTrue(result.isPresent());
        assertThat(result_find_first.get().getName(), is(r1.getName()));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_400_page_min_value() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(-1, Integer.MAX_VALUE, "name", "asc", "b"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_400_size_min_value() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParams(utilEvaluation.allPages(0, 0, "name", "desc", "a"))
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("m");
        var r3 = utilEvaluation.savedRegionCountedProj("z");
        var search = new SearchRegionEvaluationDetailsRequest("");
        var pageDetails = new PageRequest(0, Integer.MAX_VALUE, "name", "desc");
        var response = evaluationFacade.searchRegionEvaluationDetails(search, pageDetails);
        var foundRegion =  response.getPage().getContent().get(0);
        assertEquals(foundRegion.getName(), r3.getName());
        assertNotNull(response.getPage().getContent());
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_min_page_value() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(-1, 10, "name", "desc");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_min_size_value() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 0, "name", "desc");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_null_page_value() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(null, Integer.MAX_VALUE, "name", "desc");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_null_size_value() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, null, "name", "DESC");
        assertThrows(
                NullPointerException.class,
                () -> evaluationFacade.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_testService() {
        var r1 = utilEvaluation.savedRegionCountedProj("a");
        var r2 = utilEvaluation.savedRegionCountedProj("m");
        var r3 = utilEvaluation.savedRegionCountedProj("z");
        var search = new SearchRegionEvaluationDetailsRequest("a");
        var pageDetails = new PageRequest(0, Integer.MAX_VALUE, "name", "asc");
        var foundRegions = evaluationService.searchRegionEvaluationDetails(search, pageDetails);
        var foundRegion =  foundRegions.getContent().get(0);
        assertEquals(foundRegion.getName(), r1.getName());
        assertNotNull(foundRegions.getContent());
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_testService_null_page_param() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(null, Integer.MAX_VALUE, "name", "asc");
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_testService_null_size_param() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, null, "name", "asc");
        assertThrows(
                NullPointerException.class,
                () -> evaluationService.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_testService_min_size_param() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(0, 0, "name", "asc");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationService.searchRegionEvaluationDetails(search, pageDetails));
    }

    @Test
    public void searchRegionEvaluationDetailsForManagement_testService_min_page_param() {
        var search = new SearchRegionEvaluationDetailsRequest("b");
        var pageDetails = new PageRequest(-1, Integer.MAX_VALUE, "name", "desc");
        assertThrows(
                IllegalArgumentException.class,
                () -> evaluationService.searchRegionEvaluationDetails(search, pageDetails));
    }
}
