package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.web.model.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.LocationApi.COUNTRY_SAVE;
import static com.bloxico.ase.userservice.web.api.LocationApi.REGION_SAVE;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilLocation utilLocation;

    @Test
    public void createRegion_409_regionAlreadyExists() {
        var auth = utilAuth.doAdminAuthentication();
        var request = new SaveRegionRequest(genUUID());
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGION_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + REGION_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(REGION_EXISTS.getCode()));
    }

    @Test
    public void createRegion_200_ok() {
        var regionName = genUUID();
        var region = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new SaveRegionRequest(regionName))
                .when()
                .post(API_URL + REGION_SAVE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveRegionResponse.class)
                .getRegion();
        assertNotNull(region.getId());
        assertEquals(regionName, region.getName());
    }

    @Test
    public void createCountry_409_countryAlreadyExists() {
        var auth = utilAuth.doAdminAuthentication();
        var region = utilLocation.savedRegion();
        var request = new SaveCountryRequest(genUUID(), region.getName());
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    public void createCountry_404_regionNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new SaveCountryRequest(genUUID(), genUUID()))
                .when()
                .post(API_URL + COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    public void createCountry_200_ok() {
        var region = utilLocation.savedRegionDto();
        var regionName = region.getName();
        var countryName = genUUID();
        var country = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new SaveCountryRequest(countryName, regionName))
                .when()
                .post(API_URL + COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveCountryResponse.class)
                .getCountry();
        assertNotNull(country.getId());
        assertEquals(countryName, country.getName());
        assertNotNull(region.getId());
        assertEquals(region, country.getRegion());
    }

}
