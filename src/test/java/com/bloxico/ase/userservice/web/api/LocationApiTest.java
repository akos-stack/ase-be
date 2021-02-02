package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.MockUtil.ERROR_CODE;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.web.api.LocationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.REGION_EXISTS;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void findAllCountries_200_ok() {
        mockUtil.savedCountry();
        mockUtil.savedCountry();
        mockUtil.savedCountry();
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + COUNTRIES)
                .then()
                .assertThat()
                .statusCode(200)
                .body("countries.size()", is(3));
    }

    @Test
    public void findAllCities_200_ok() {
        mockUtil.savedCity();
        mockUtil.savedCity();
        mockUtil.savedCity();
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + CITIES)
                .then()
                .assertThat()
                .statusCode(200)
                .body("cities.size()", is(3));
    }

    @Test
    public void createRegion_200_regionSuccessfullyCreated() {
        var regionName = uuid();
        var request = new CreateRegionRequest(regionName);
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGIONS_CREATE)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "region.id", notNullValue(),
                        "region.name", is(regionName),
                        "region.number_of_countries", is(0),
                        "region.number_of_evaluators", is(0));
    }

    @Test
    public void createRegion_409_regionAlreadyExists() {
        var adminBearerToken = mockUtil.doAdminAuthentication();
        var request = new CreateRegionRequest(uuid());
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGIONS_CREATE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGIONS_CREATE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(REGION_EXISTS.getCode()));
    }


}
