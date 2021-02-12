package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataManagementApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkMetadataManagementApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;

    @Test
    public void createCategory_NotAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
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
                .header("Authorization", utilAuth.doAdminAuthentication())
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
        var metadataDto = utilArtworkMetadata.savedCategoryDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest(metadataDto.getName(), ArtworkMetadataStatus.PENDING, CATEGORY))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteCategory_success() {
        var metadataDto = utilArtworkMetadata.savedCategoryDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("name", metadataDto.getName())
                .param("type", CATEGORY.name())
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchCategories_notAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void fetchCategories_success() {
        var dto1 = utilArtworkMetadata.savedCategoryDto();
        var dto2 = utilArtworkMetadata.savedCategoryDto();
        var dto3 = utilArtworkMetadata.savedCategoryDto();

        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedArtworkMetadataResponse.class)
                .getEntries();
        assertThat(response, hasItems(dto1, dto2, dto3));
    }

    @Test
    public void createMaterial_NotAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
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
                .header("Authorization", utilAuth.doAdminAuthentication())
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
        var metadataDto = utilArtworkMetadata.savedMaterialDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest(metadataDto.getName(), ArtworkMetadataStatus.PENDING, MATERIAL))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteMaterial_success() {
        var metadataDto = utilArtworkMetadata.savedMaterialDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("name", metadataDto.getName())
                .param("type", MATERIAL.name())
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchMaterials_notAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void fetchMaterials_success() {
        var dto1 = utilArtworkMetadata.savedMaterialDto();
        var dto2 = utilArtworkMetadata.savedMaterialDto();
        var dto3 = utilArtworkMetadata.savedMaterialDto();

        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedArtworkMetadataResponse.class)
                .getEntries();
        assertThat(response, hasItems(dto1, dto2, dto3));
    }

    @Test
    public void createMedium_NotAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
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
                .header("Authorization", utilAuth.doAdminAuthentication())
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
        var metadataDto = utilArtworkMetadata.savedMediumDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest(metadataDto.getName(), ArtworkMetadataStatus.PENDING, MEDIUM))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteMedium_success() {
        var metadataDto = utilArtworkMetadata.savedMediumDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("name", metadataDto.getName())
                .param("type", MEDIUM.name())
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchMediums_notAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void fetchMediums_success() {
        var dto1 = utilArtworkMetadata.savedMediumDto();
        var dto2 = utilArtworkMetadata.savedMediumDto();
        var dto3 = utilArtworkMetadata.savedMediumDto();

        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedArtworkMetadataResponse.class)
                .getEntries();
        assertThat(response, hasItems(dto1, dto2, dto3));
    }

    @Test
    public void createStyle_NotAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
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
                .header("Authorization", utilAuth.doAdminAuthentication())
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
        var metadataDto = utilArtworkMetadata.savedStyleDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .body(new ArtworkMetadataUpdateRequest(metadataDto.getName(), ArtworkMetadataStatus.PENDING, STYLE))
                .when()
                .post(API_URL + UPDATE_METADATA_STATUS)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteStyle_success() {
        var metadataDto = utilArtworkMetadata.savedStyleDto();

        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("name", metadataDto.getName())
                .param("type", STYLE.name())
                .when()
                .delete(API_URL + DELETE_METADATA)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetchStyles_notAuthorized() {
        given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", STYLE.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    public void fetchStyles_success() {
        var dto1 = utilArtworkMetadata.savedStyleDto();
        var dto2 = utilArtworkMetadata.savedStyleDto();
        var dto3 = utilArtworkMetadata.savedStyleDto();

        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .contentType(JSON)
                .param("type", STYLE.name())
                .when()
                .get(API_URL + SEARCH_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedArtworkMetadataResponse.class)
                .getEntries();
        assertThat(response, hasItems(dto1, dto2, dto3));
    }
}
