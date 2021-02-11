package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.userservice.web.model.address.CreateCountryRequest;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.web.api.LocationApi.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Location.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilLocation utilLocation;

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

}
