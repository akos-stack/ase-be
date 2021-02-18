package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.web.model.artwork.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.web.api.ArtworkMetadataApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkMetadataApiTest extends AbstractSpringTest {

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;

    @Test
    public void saveArtworkMetadata_200_ok() {
        for (var type : Type.values()) {
            var name = genUUID();
            assertNull(utilArtworkMetadata.findArtworkMetadataDto(type, name));
            var response = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .contentType(JSON)
                    .body(new SaveArtworkMetadataRequest(name, type))
                    .when()
                    .post(API_URL + ARTWORK_METADATA_SAVE)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SaveArtworkMetadataResponse.class);
            assertNotNull(utilArtworkMetadata.findArtworkMetadataDto(type, name));
            assertNotNull(response.getArtworkMetadata().getId());
            assertEquals(name, response.getArtworkMetadata().getName());
            assertEquals(APPROVED, response.getArtworkMetadata().getStatus());
        }
    }

    // TODO-test updateArtworkMetadata_404_notFound

    @Test
    public void updateArtworkMetadata_200_ok() {
        for (var type : Type.values()) {
            var status = randEnumConst(Status.class);
            var metadata = utilArtworkMetadata.savedArtworkMetadataDto(type, status);
            var newStatus = randOtherEnumConst(status);
            var response = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .contentType(JSON)
                    .body(new UpdateArtworkMetadataRequest(metadata.getName(), newStatus, type))
                    .when()
                    .post(API_URL + ARTWORK_METADATA_UPDATE)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(UpdateArtworkMetadataResponse.class);
            assertSame(newStatus, response.getArtworkMetadata().getStatus());
            assertEquals(metadata.getName(), response.getArtworkMetadata().getName());
        }
    }

    // TODO-test deleteArtworkMetadata_404_notFound

    @Test
    public void deleteArtworkMetadata_200_ok() {
        for (var type : Type.values()) {
            var name = utilArtworkMetadata.savedArtworkMetadataDto(type).getName();
            assertNotNull(utilArtworkMetadata.findArtworkMetadataDto(type, name));
            given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .contentType(JSON)
                    .param("name", name)
                    .param("type", type.name())
                    .when()
                    .delete(API_URL + ARTWORK_METADATA_DELETE)
                    .then()
                    .assertThat()
                    .statusCode(200);
            assertNull(utilArtworkMetadata.findArtworkMetadataDto(type, name));
        }
    }

    @Test
    public void searchMetadata_200_ok() {
        for (var type : Type.values()) {
            var m1 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m2 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m3 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m4 = utilArtworkMetadata.savedArtworkMetadataDto(type, randOtherEnumConst(APPROVED));
            var m5 = utilArtworkMetadata.savedArtworkMetadataDto(randOtherEnumConst(type), APPROVED);
            var response = given()
                    .header("Authorization", utilAuth.doAdminAuthentication())
                    .contentType(JSON)
                    .param("type", type.name())
                    .param("size", Integer.MAX_VALUE)
                    .when()
                    .get(API_URL + ARTWORK_METADATA_SEARCH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(PagedArtworkMetadataResponse.class);
            assertThat(
                    response.getEntries(),
                    allOf(
                            hasItems(m1, m2, m3, m4),
                            not(hasItems(m5))));
        }
    }

    @Test
    public void searchApprovedArtworkMetadata_200_ok() {
        for (var type : Type.values()) {
            var m1 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m2 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m3 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            var m4 = utilArtworkMetadata.savedArtworkMetadataDto(type, randOtherEnumConst(APPROVED));
            var m5 = utilArtworkMetadata.savedArtworkMetadataDto(randOtherEnumConst(type), APPROVED);
            var response = given()
                    .header("Authorization", utilAuth.doAuthentication())
                    .contentType(JSON)
                    .param("type", type.name())
                    .param("size", Integer.MAX_VALUE)
                    .when()
                    .get(API_URL + ARTWORK_METADATA_APPROVED)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(SearchArtworkMetadataResponse.class);
            assertThat(
                    response.getEntries(),
                    allOf(
                            hasItems(m1, m2, m3),
                            not(hasItems(m4, m5))));
        }
    }

}
