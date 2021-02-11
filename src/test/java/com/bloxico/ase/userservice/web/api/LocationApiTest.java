package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.web.model.address.SearchCitiesResponse;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.LocationApi.CITIES;
import static com.bloxico.ase.userservice.web.api.LocationApi.COUNTRIES;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class LocationApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilLocation utilLocation;

    @Test
    public void findAllCountries_200_ok() {
        var c1 = utilLocation.savedCountryDto();
        var c2 = utilLocation.savedCountryDto();
        var c3 = utilLocation.savedCountryDto();
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

}
