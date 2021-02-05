package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.SEARCH_ARTWORK_METADATA;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataManagementApi.CREATE_METADATA;
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
                .body(new ArtworkMetadataCreateRequest("category", CATEGORY))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY)
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
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
                .body(new ArtworkMetadataCreateRequest("material", MATERIAL))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL)
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
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
                .body(new ArtworkMetadataCreateRequest("medium", MEDIUM))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM)
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
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
                .body(new ArtworkMetadataCreateRequest("style", STYLE))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM)
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }
}
