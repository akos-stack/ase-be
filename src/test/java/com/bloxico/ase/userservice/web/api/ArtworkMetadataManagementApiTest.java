package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
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
public class ArtworkMetadataManagementApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void createCategory_NotAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("category", CATEGORY))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void createCategory_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("category", CATEGORY))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateCategoryStatus_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest("category", ArtworkMetadataStatus.PENDING, CATEGORY))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteCategory_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("name", "category")
                .param("type", CATEGORY)
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchCategories_notAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void createMaterial_NotAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("material", MATERIAL))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void createMaterial_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("material", MATERIAL))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateMaterialStatus_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest("material", ArtworkMetadataStatus.PENDING, MATERIAL))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteMaterial_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("name", "material")
                .param("type", MATERIAL)
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchMaterials_notAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void createMedium_NotAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("medium", MEDIUM))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void createMedium_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("medium", MEDIUM))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateMediumStatus_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest("medium", ArtworkMetadataStatus.PENDING, MEDIUM))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteMedium_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("name", "medium")
                .param("type", MEDIUM)
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchMediums_notAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }

    @Test
    public void createStyle_NotAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("style", STYLE))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void createStyle_success() {
        given()
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataCreateRequest("style", STYLE))
                .when()
                .post(API_URL + CREATE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateStyleStatus_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest("style", ArtworkMetadataStatus.PENDING, STYLE))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteStyle_success() {
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("name", "style")
                .param("type", STYLE)
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchStyles_notAuthorized() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .param("type", STYLE)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
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
                .header("Authorization", mockUtil.doAdminAuthentication())
                .contentType(JSON)
                .param("type", STYLE)
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .body("entries", not(isEmptyOrNullString()));
    }
}
