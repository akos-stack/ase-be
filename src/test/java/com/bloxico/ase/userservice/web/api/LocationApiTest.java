package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.web.api.LocationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilLocation utilLocation;
    @Autowired private RegionRepository regionRepository;
    @Autowired private CountryRepository countryRepository;

    @Test
    public void findAllCountries_200_ok() {
        var c1 = utilLocation.savedCountryProj();
        var c2 = utilLocation.savedCountryProj();
        var c3 = utilLocation.savedCountryProj();
        var countries = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
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
    public void findAllCities_200_ok() {
        var c1 = utilLocation.savedCityDto();
        var c2 = utilLocation.savedCityDto();
        var c3 = utilLocation.savedCityDto();
        var cities = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + CITIES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchCitiesResponse.class)
                .getCities();
        assertThat(cities, hasItems(c1, c2, c3));
    }

    @Test
    public void findAllRegions_200_ok() {
        var searchTerm = genUUID();
        var r1 = utilLocation.savedRegionProjWithName(genEmail(searchTerm));
        var r2 = utilLocation.savedRegionProjWithName(genEmail(searchTerm.toUpperCase()));
        var r3 = utilLocation.savedRegionProjWithName(genEmail(searchTerm.toLowerCase()));
        var r4 = utilLocation.savedRegionProjWithName(genEmail("fooBar"));
        var regions = given()
                .header("Authorization", utilAuth.doAuthentication())
                .queryParam("search", searchTerm)
                .when()
                .get(API_URL + REGIONS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchRegionsResponse.class)
                .getRegions();
        assertThat(regions, allOf(hasItems(r1, r2, r3), not(hasItems(r4))));
    }

    @Test
    public void createRegion_200_ok() {
        var regionName = genUUID();
        var request = new CreateRegionRequest(regionName);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
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
        var adminBearerToken = utilAuth.doAdminAuthentication();
        var request = new CreateRegionRequest(genUUID());
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

    @Test
    public void deleteRegion_200_ok() {
        var region = utilLocation.savedRegion();
        assertTrue(regionRepository.findById(region.getId()).isPresent());
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", region.getId())
                .when()
                .delete(API_URL + REGIONS_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        assertTrue(regionRepository.findById(region.getId()).isEmpty());
    }

    @Test
    public void deleteRegion_404_regionNotFound() {
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", -1)
                .when()
                .delete(API_URL + REGIONS_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    public void deleteRegion_400_regionHasCountries() {
        var adminBearerToken = utilAuth.doAdminAuthentication();
        var region = utilLocation.savedRegionDto();
        var request = new CreateCountryRequest(genUUID(), region.getName(), 10, 40);
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRIES_CREATE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .pathParam("id", region.getId())
                .when()
                .delete(API_URL + REGIONS_DELETE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(REGION_DELETE_OPERATION_NOT_SUPPORTED.getCode()));
    }

    @Test
    public void createCountry_200_ok() {
        var region = utilLocation.savedRegion();
        var request = new CreateCountryRequest(genUUID(), region.getName(), 10, 40);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRIES_CREATE)
                .then()
                .assertThat()
                .statusCode(200)
                .body(
                        "country", notNullValue(),
                        "country.id", notNullValue(),
                        "country.name", is(request.getName()),
                        "country.region", notNullValue(),
                        "country.region.id", is(region.getId()),
                        "country.region.name", is(region.getName()),
                        "country.evaluation_details.price_per_evaluation", is(10),
                        "country.evaluation_details.availability_percentage", is(40),
                        "country.evaluation_details.total_of_evaluators", is(0));
    }

    @Test
    public void createCountry_409_countryAlreadyExists() {
        var adminBearerToken = utilAuth.doAdminAuthentication();
        var region = utilLocation.savedRegion();
        var request = new CreateCountryRequest(genUUID(), region.getName(), 10, 40);
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRIES_CREATE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", adminBearerToken)
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRIES_CREATE)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    public void createCountry_404_regionNotFound() {
        var request = new CreateCountryRequest(genUUID(), genUUID(), 10, 40);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + COUNTRIES_CREATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

    @Test
    public void editCountry_200_ok() {
        var countryId = utilLocation.savedCountry().getId();
        var region = utilLocation.savedRegion();
        var request = new EditCountryRequest(genUUID(), region.getName(), 5, 55);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", countryId)
                .body(request)
                .when()
                .put(API_URL + COUNTRIES_EDIT)
                .then()
                .assertThat()
                .statusCode(200);

        var editedCountry = countryRepository
                .findById(countryId)
                .orElse(null);

        assertNotNull(editedCountry);
        assertEquals(request.getName(), editedCountry.getName());
        assertEquals(request.getRegion(), editedCountry.getRegion().getName());
        assertEquals(request.getPricePerEvaluation().intValue(),
                editedCountry.getCountryEvaluationDetails().getPricePerEvaluation());
        assertEquals(request.getAvailabilityPercentage().intValue(),
                editedCountry.getCountryEvaluationDetails().getAvailabilityPercentage());
    }

    @Test
    public void editCountry_404_countryAlreadyExists() {
        var country1 = utilLocation.savedCountry();
        var country2 = utilLocation.savedCountry();
        var request =
                new EditCountryRequest(country2.getName(), country1.getRegion().getName(), 5, 55);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", country1.getId())
                .body(request)
                .when()
                .put(API_URL + COUNTRIES_EDIT)
                .then()
                .assertThat()
                .statusCode(409)
                .body(ERROR_CODE, is(COUNTRY_EXISTS.getCode()));
    }

    @Test
    public void editCountry_404_countryNotFound() {
        var country = utilLocation.savedCountry();
        var request = new EditCountryRequest(genUUID(), country.getRegion().getName(), 5, 55);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", -1)
                .body(request)
                .when()
                .put(API_URL + COUNTRIES_EDIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(COUNTRY_NOT_FOUND.getCode()));
    }

    @Test
    public void editCountry_404_regionNotFound() {
        var country = utilLocation.savedCountry();
        var request = new EditCountryRequest(genUUID(), genUUID(), 5, 55);
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .pathParam("id", country.getId())
                .body(request)
                .when()
                .put(API_URL + COUNTRIES_EDIT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(REGION_NOT_FOUND.getCode()));
    }

}
