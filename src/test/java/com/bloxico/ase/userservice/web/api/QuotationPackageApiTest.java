package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.quotationpackage.QuotationPackageDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.bloxico.ase.userservice.web.api.QuotationPackageApi.QUOTATION_PACKAGES_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.QuotationPackageApi.QUOTATION_PACKAGE_CREATE_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class QuotationPackageApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void allQuotationPackages_200_quotationPackagesSuccessfullyRetrieved() {
        var names = Arrays.asList("Basic", "Advanced", "Pro");
        var expectedList = mockUtil
                .savedQuotationPackageDtos(names);

        var actualList = given()
                .header("Authorization", mockUtil.doAuthentication())
                .when()
                .get(API_URL + QUOTATION_PACKAGES_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("quotation_packages", QuotationPackageDto.class);

        assertEquals(expectedList, actualList);
    }

    @Test
    public void createQuotationPackage_200_quotationPackageSuccessfullyCreated() {
        var request =
                mockUtil.doCreateQuotationPackageRequest();

        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + QUOTATION_PACKAGE_CREATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "name", is(request.getName()),
                        "description", is(request.getDescription()),
                        "image_path", is(request.getImagePath()),
                        "number_of_evaluations", is(request.getNumberOfEvaluations()),
                        "active", is(request.getActive()));
    }

    @Test
    public void createQuotationPackage_409_quotationPackageAlreadyExists() {
        var adminBearerToken = mockUtil.doAdminAuthentication();

        var request = mockUtil
                .doCreateQuotationPackageRequest("Basic");

        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + QUOTATION_PACKAGE_CREATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + QUOTATION_PACKAGE_CREATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(409);
    }

}
