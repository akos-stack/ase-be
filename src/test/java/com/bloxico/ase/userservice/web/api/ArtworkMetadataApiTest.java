package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.FETCH_CATEGORIES;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.FETCH_MATERIALS;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.FETCH_MEDIUMS;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.FETCH_STYLES;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataManagementApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ArtworkMetadataApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void fetchCategories_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("category"))
                .when()
                .post(API_URL + CREATE_CATEGORY)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + FETCH_CATEGORIES)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void fetchMaterials_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("material"))
                .when()
                .post(API_URL + CREATE_MATERIAL)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + FETCH_MATERIALS)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void fetchMediums_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("medium"))
                .when()
                .post(API_URL + CREATE_MEDIUM)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + FETCH_MEDIUMS)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void fetchStyles_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("style"))
                .when()
                .post(API_URL + CREATE_STYLE)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .when()
                .get(API_URL + FETCH_STYLES)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }
}
