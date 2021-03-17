package com.bloxico.ase.userservice.web.model;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionEvaluationDetailsResponse;
import com.bloxico.ase.userservice.web.model.user.SearchUsersResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.web.api.EvaluationApi.EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH;
import static com.bloxico.ase.userservice.web.api.UserManagementApi.USER_SEARCH_ENDPOINT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class PageRequestTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilEvaluation utilEvaluation;
    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;

    @Test
    public void pageRequest_400_nullPage() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("size", 5)
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_400_invalidPage() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("page", -1)
                .queryParam("size", 5)
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_400_nullSize() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("page", 0)
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_400_invalidSize() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("page", 0)
                .queryParam("size", 0)
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_400_sortByEmpty() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "")
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_400_sortByBlank() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", "")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "  ")
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_200_singlePage() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r2 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r3 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r4 = utilEvaluation.savedRegionCountedProj(genUUID());
        var page = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", region)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage();
        assertEquals(1, page.getNumberOfPages());
        assertEquals(10, page.getSize());
        assertEquals(3, page.getTotalSize());
        assertThat(
                page.getContent(),
                allOf(
                        hasItems(r1, r2, r3),
                        not(hasItems(r4))));
    }

    @Test
    public void pageRequest_200_multiplePages() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r2 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r3 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r4 = utilEvaluation.savedRegionCountedProj(genUUID());
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertThat(
                    page.getContent(),
                    allOf(
                            hasItems(r1, r2),
                            not(hasItems(r3, r4))));
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertThat(
                    page.getContent(),
                    allOf(
                            hasItems(r3),
                            not(hasItems(r1, r2, r4))));
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 2)
                    .queryParam("size", 2)
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByEmail_safeSort() {
        var email = genEmail();
        var u1 = utilUser.savedUserDtoWithEmail("ac" + email);
        var u2 = utilUser.savedUserDtoWithEmail("aa" + email);
        var u3 = utilUser.savedUserDtoWithEmail("ab" + email);
        utilUser.savedUserDto();
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "email")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(u2, u3), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "email")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(u1), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByNameAndEmail_safeSort() {
        var email = genUUID();
        var u1 = utilUser.savedUserDtoWithEmailAndName("ac" + email, "AUser");
        var u2 = utilUser.savedUserDtoWithEmailAndName("ad" + email, "AUser");
        var u3 = utilUser.savedUserDtoWithEmailAndName("ab" + email, "AUser");
        var u4 = utilUser.savedUserDtoWithEmailAndName("aa" + email, "BUser");
        utilUser.savedUserDto();
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "name,email")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(4, page.getTotalSize());
            assertEquals(List.of(u3, u1), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "name,email")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(4, page.getTotalSize());
            assertEquals(List.of(u2, u4), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByNameAndEmailWithOrderDesc_safeSort() {
        var email = genUUID();
        var u1 = utilUser.savedUserDtoWithEmailAndName("ac" + email, "AUser");
        var u2 = utilUser.savedUserDtoWithEmailAndName("ad" + email, "AUser");
        var u3 = utilUser.savedUserDtoWithEmailAndName("ab" + email, "AUser");
        var u4 = utilUser.savedUserDtoWithEmailAndName("aa" + email, "BUser");
        utilUser.savedUserDto();
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "name,email")
                    .queryParam("order", "DESC")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(4, page.getTotalSize());
            assertEquals(List.of(u4, u2), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("email", email)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "name,email")
                    .queryParam("order", "DESC")
                    .when()
                    .get(API_URL + USER_SEARCH_ENDPOINT)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchUsersResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(4, page.getTotalSize());
            assertEquals(List.of(u1, u3), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByNameAndEmailWithInvalidOrder_safeSort() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("email", genEmail())
                .queryParam("page", 0)
                .queryParam("size", 2)
                .queryParam("sort", "name,email")
                .queryParam("order", "foo")
                .when()
                .get(API_URL + USER_SEARCH_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_200_sortedByName_unsafeSort() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj("ac" + region);
        var r2 = utilEvaluation.savedRegionCountedProj("aa" + region);
        var r3 = utilEvaluation.savedRegionCountedProj("ab" + region);
        utilEvaluation.savedRegionCountedProj(genUUID());
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "name")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r2, r3), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "name")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r1), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByIdAndName_unsafeSort() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj("ac" + region);
        var r2 = utilEvaluation.savedRegionCountedProj("aa" + region);
        var r3 = utilEvaluation.savedRegionCountedProj("ab" + region);
        utilEvaluation.savedRegionCountedProj(genUUID());
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "id,name")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r1, r2), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "id,name")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r3), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByIdAndNameWithOrderDesc_unsafeSort() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj("ac" + region);
        var r2 = utilEvaluation.savedRegionCountedProj("aa" + region);
        var r3 = utilEvaluation.savedRegionCountedProj("ab" + region);
        utilEvaluation.savedRegionCountedProj(genUUID());
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("sort", "id,name")
                    .queryParam("order", "DESC")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r3, r2), page.getContent());
        }
        {
            var page = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .queryParam("search", region)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .queryParam("sort", "id,name")
                    .queryParam("order", "DESC")
                    .when()
                    .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchRegionEvaluationDetailsResponse.class)
                    .getPage();
            assertEquals(2, page.getNumberOfPages());
            assertEquals(2, page.getSize());
            assertEquals(3, page.getTotalSize());
            assertEquals(List.of(r1), page.getContent());
        }
    }

    @Test
    public void pageRequest_200_sortedByIdAndNameWithInvalidOrder_unsafeSort() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", genUUID())
                .queryParam("page", 0)
                .queryParam("size", 2)
                .queryParam("sort", "id,name")
                .queryParam("order", "foo")
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void pageRequest_200_withOrderWithoutSort_unsafeSort() {
        var region = genUUID();
        var r1 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r2 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r3 = utilEvaluation.savedRegionCountedProj(genWithSubstring(region));
        var r4 = utilEvaluation.savedRegionCountedProj(genUUID());
        var page = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .queryParam("search", region)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("order", "DESC")
                .when()
                .get(API_URL + EVALUATION_MANAGEMENT_REGION_DETAILS_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchRegionEvaluationDetailsResponse.class)
                .getPage();
        assertEquals(1, page.getNumberOfPages());
        assertEquals(10, page.getSize());
        assertEquals(3, page.getTotalSize());
        assertThat(
                page.getContent(),
                allOf(
                        hasItems(r1, r2, r3),
                        not(hasItems(r4))));
    }

}
