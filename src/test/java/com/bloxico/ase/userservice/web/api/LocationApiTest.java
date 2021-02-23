package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.LocationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilLocation utilLocation;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void findAllRegions_200_ok() {
        var r1 = utilLocation.savedRegionDto();
        var r2 = utilLocation.savedRegionDto();
        var r3 = utilLocation.savedRegionDto();

        var regions = given()
                .header("Authorization", utilAuth.doAuthentication())
                .when()
                .get(API_URL + REGIONS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchRegionsResponse.class)
                .getRegions();

        assertThat(regions, hasItems(r1, r2, r3));
    }

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
    public void deleteRegion_400_regionHasCountries() {
        var region = utilLocation.savedRegion();
        utilLocation.savedCountryWithRegion(region);

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .when()
                .pathParam("id", region.getId())
                .post(API_URL + REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(REGION_DELETE_OPERATION_NOT_SUPPORTED.getCode()));
    }

    @Test
    public void deleteRegion_404_regionNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .when()
                .pathParam("id", -1)
                .post(API_URL + REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    public void deleteRegion_200_ok() {
        var region = utilLocation.savedRegion();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .when()
                .pathParam("id", region.getId())
                .post(API_URL + REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        assertTrue(regionRepository.findById(region.getId()).isEmpty());
    }

    @Test
    public void findAllCountries_200_ok() {
        var c1 = utilLocation.savedCountryDto();
        var c2 = utilLocation.savedCountryDto();
        var c3 = utilLocation.savedCountryDto();

        var countries = given()
                .header("Authorization", utilAuth.doAuthentication())
                .when()
                .get(API_URL + COUNTRIES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCountriesResponse.class)
                .getCountries();

        assertThat(countries, hasItems(c1, c2, c3));
    }

    @Test
    public void createCountry_409_countryAlreadyExists() {
        var auth = utilAuth.doAdminAuthentication();
        var region = utilLocation.savedRegion();
        var request = new SaveCountryRequest(genUUID(), Set.of(region.getName()));
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
                .body(new SaveCountryRequest(genUUID(), Set.of(genUUID())))
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
                .body(new SaveCountryRequest(countryName, Set.of(regionName)))
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
        assertThat(country.getRegions(), hasItems(region));
    }

    @Test
    public void updateCountry_404_countryNotFound() {
        var region = utilLocation.savedRegion();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", -1)
                .body(new UpdateCountryRequest(genUUID(), Set.of(region.getName())))
                .when()
                .post(API_URL + COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    public void updateCountry_404_regionNotFound() {
        var country = utilLocation.savedCountry();
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", country.getId())
                .body(new UpdateCountryRequest(genUUID(), Set.of(genUUID())))
                .when()
                .post(API_URL + COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    public void updateCountry_409_countryAlreadyExists() {
        var region = utilLocation.savedRegion();
        var country1 = utilLocation.savedCountryWithRegion(region);
        var country2 = utilLocation.savedCountryWithRegion(region);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", country1.getId())
                .body(new UpdateCountryRequest(country2.getName(), Set.of(region.getName())))
                .when()
                .post(API_URL + COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    public void updateCountry_200_ok() {
        var country = utilLocation.savedCountry();
        var region = utilLocation.savedRegionDto();
        var newCountryName = genUUID();
        var newRegionName = region.getName();
        var updatedCountry = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", country.getId())
                .body(new UpdateCountryRequest(newCountryName, Set.of(newRegionName)))
                .when()
                .post(API_URL + COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UpdateCountryResponse.class)
                .getCountry();
        assertEquals(newCountryName, updatedCountry.getName());
        assertThat(updatedCountry.getRegions(), hasItems(region));
    }

}
