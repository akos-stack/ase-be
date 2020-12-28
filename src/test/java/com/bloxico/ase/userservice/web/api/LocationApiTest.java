package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.LocationApi.CITIES;
import static com.bloxico.ase.userservice.web.api.LocationApi.COUNTRIES;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void allCountries_200_ok() {
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
                .body("size()", is(3));
    }

    @Test
    public void allCities_200_ok() {
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
                .body("size()", is(3));
    }

}
