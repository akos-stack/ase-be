package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.user.Role;
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

    @Autowired private UtilLocation utilLocation;
    @Autowired private RegionRepository regionRepository;
    @Autowired private UtilSecurityContext utilSecurityContext;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void findAllRegions_200_ok() {
        var r1 = utilLocation.savedRegionDto();
        var r2 = utilLocation.savedRegionDto();
        var r3 = utilLocation.savedRegionDto();

        var regions = given()
                .header("Authorization", utilSecurityContext.getToken())
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
    @WithMockCustomUser(auth = true)
    public void saveRegion_409_regionAlreadyExists() {
        var auth = utilSecurityContext.getToken();
        var request = new SaveRegionRequest(genUUID());
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_REGION_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_REGION_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(REGION_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveRegion_200_ok() {
        var regionName = genUUID();
        var region = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new SaveRegionRequest(regionName))
                .when()
                .post(API_URL + MNG_REGION_SAVE)
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
    @WithMockCustomUser(auth = true)
    public void deleteRegion_409_regionHasCountries() {
        var region = utilLocation.savedRegion();
        utilLocation.savedCountryWithRegion(region);

        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", region.getId())
                .when()
                .delete(API_URL + MNG_REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(REGION_DELETE_OPERATION_NOT_SUPPORTED.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteRegion_404_regionNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", -1L)
                .when()
                .delete(API_URL + MNG_REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteRegion_200_ok() {
        var region = utilLocation.savedRegion();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .param("id", region.getId())
                .when()
                .delete(API_URL + MNG_REGION_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        assertTrue(regionRepository.findById(region.getId()).isEmpty());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void findAllCountries_200_ok() {
        var c1 = utilLocation.savedCountryDto();
        var c2 = utilLocation.savedCountryDto();
        var c3 = utilLocation.savedCountryDto();

        var countries = given()
                .header("Authorization", utilSecurityContext.getToken())
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
    @WithMockCustomUser(auth = true)
    public void saveCountry_409_countryAlreadyExists() {
        var auth = utilSecurityContext.getToken();
        var region = utilLocation.savedRegion();
        var request = new SaveCountryRequest(genUUID(), Set.of(region.getName()));
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MGN_COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", auth)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MGN_COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveCountry_404_regionNotFound() {
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new SaveCountryRequest(genUUID(), Set.of(genUUID())))
                .when()
                .post(API_URL + MGN_COUNTRY_SAVE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveCountry_200_ok() {
        var region = utilLocation.savedRegionDto();
        var regionName = region.getName();
        var countryName = genUUID();
        var country = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new SaveCountryRequest(countryName, Set.of(regionName)))
                .when()
                .post(API_URL + MGN_COUNTRY_SAVE)
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
    @WithMockCustomUser(auth = true)
    public void updateCountry_404_countryNotFound() {
        var region = utilLocation.savedRegion();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new UpdateCountryRequest(-1L, genUUID(), Set.of(region.getName())))
                .when()
                .post(API_URL + MNG_COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateCountry_404_regionNotFound() {
        var country = utilLocation.savedCountry();
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new UpdateCountryRequest(country.getId(), genUUID(), Set.of(genUUID())))
                .when()
                .post(API_URL + MNG_COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateCountry_409_countryAlreadyExists() {
        var region = utilLocation.savedRegion();
        var country1 = utilLocation.savedCountryWithRegion(region);
        var country2 = utilLocation.savedCountryWithRegion(region);
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new UpdateCountryRequest(country1.getId(), country2.getName(), Set.of(region.getName())))
                .when()
                .post(API_URL + MNG_COUNTRY_UPDATE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateCountry_200_ok() {
        var country = utilLocation.savedCountry();
        var region = utilLocation.savedRegionDto();
        var newCountryName = genUUID();
        var newRegionName = region.getName();
        var updatedCountry = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(new UpdateCountryRequest(country.getId(), newCountryName, Set.of(newRegionName)))
                .when()
                .post(API_URL + MNG_COUNTRY_UPDATE)
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
