package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkMetadataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.SEARCH_ARTWORK_METADATA;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkMetadataApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;

    @Test
    public void fetchCategories_success() {
        var c1 = utilArtworkMetadata.savedCategoryDto();
        var c2 = utilArtworkMetadata.savedCategoryDto();
        var c3 = utilArtworkMetadata.savedCategoryDto();
        var metadata = given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", CATEGORY.name())
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkMetadataResponse.class)
                .getEntries();
        assertThat(metadata, hasItems(c1, c2, c3));
    }

    @Test
    public void fetchMaterials_success() {
        var c1 = utilArtworkMetadata.savedMaterialDto();
        var c2 = utilArtworkMetadata.savedMaterialDto();
        var c3 = utilArtworkMetadata.savedMaterialDto();
        var metadata = given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", MATERIAL.name())
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkMetadataResponse.class)
                .getEntries();
        assertThat(metadata, hasItems(c1, c2, c3));
    }

    @Test
    public void fetchMediums_success() {
        var c1 = utilArtworkMetadata.savedMediumDto();
        var c2 = utilArtworkMetadata.savedMediumDto();
        var c3 = utilArtworkMetadata.savedMediumDto();
        var metadata = given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", MEDIUM.name())
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkMetadataResponse.class)
                .getEntries();
        assertThat(metadata, hasItems(c1, c2, c3));
    }

    @Test
    public void fetchStyles_success() {
        var c1 = utilArtworkMetadata.savedStyleDto();
        var c2 = utilArtworkMetadata.savedStyleDto();
        var c3 = utilArtworkMetadata.savedStyleDto();
        var metadata = given()
                .header("Authorization", utilAuth.doAuthentication())
                .contentType(JSON)
                .param("type", STYLE.name())
                .when()
                .get(API_URL + SEARCH_ARTWORK_METADATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkMetadataResponse.class)
                .getEntries();
        assertThat(metadata, hasItems(c1, c2, c3));
    }
}
