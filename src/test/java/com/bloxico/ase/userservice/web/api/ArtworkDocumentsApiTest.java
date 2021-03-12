package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.*;
import static com.bloxico.ase.userservice.web.api.ArtworkApi.ARTWORK_PREVIEW;
import static com.bloxico.ase.userservice.web.api.ArtworkDocumentsApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkDocumentsApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void saveArtworkDocuments_403_notAuthorized() {
        var formParams = utilArtwork.genSaveArtworkDocumentsFormParamsWithOwner(IMAGE);
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_404_notFound() {
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", -1L)
                .formParams("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_401_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoWithOwner();
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParams("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(401)
                .body(ERROR_CODE, is(ErrorCodes.User.ACCESS_NOT_ALLOWED.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_400_documentNotValid() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParams("fileCategory", CERTIFICATE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_409_documentConflict() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        for(var type: FileCategory.values()) {
            if(type == IMAGE) continue;
            byte[] doc1 = genFileBytes(type);
            var name = type == PRINCIPAL_IMAGE ? genUUID() + ".jpg" : genUUID() + ".txt";
            given()
                    .header("Authorization", securityContext.getToken())
                    .formParam("artworkId", artworkDto.getId())
                    .formParam("fileCategory", type)
                    .multiPart("documents", name, doc1)
                    .when()
                    .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                    .then()
                    .assertThat()
                    .statusCode(200);
            byte[] doc2 = genFileBytes(type);
            given()
                    .header("Authorization", securityContext.getToken())
                    .formParam("artworkId", artworkDto.getId())
                    .formParam("fileCategory", type)
                    .multiPart("documents", name, doc2)
                    .when()
                    .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                    .then()
                    .assertThat()
                    .statusCode(409)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_ALREADY_ATTACHED.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_400_documentsSize() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        for(var type: FileCategory.values()) {
            if(type == IMAGE) continue;
            byte[] doc1 = genFileBytes(type);
            byte[] doc2 = genFileBytes(type);
            var name = type == PRINCIPAL_IMAGE ? genUUID() + ".jpg" : genUUID() + ".txt";
            given()
                    .header("Authorization", securityContext.getToken())
                    .formParam("artworkId", artworkDto.getId())
                    .formParam("fileCategory", type)
                    .multiPart("documents", name, doc1)
                    .multiPart("documents", name, doc2)
                    .when()
                    .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                    .then()
                    .assertThat()
                    .statusCode(400)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments() {
        var formParams = utilArtwork.genSaveArtworkDocumentsFormParams(IMAGE);
        byte[] image = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDocuments_moreIterations() {
        var formParams = utilArtwork.genSaveArtworkDocumentsFormParams(IMAGE);
        byte[] image = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
        assertEquals(1, response.getArtworkDto().getDocuments().size());

        byte[] image2 = genFileBytes(IMAGE);
        var response2 = given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image2)
                .when()
                .post(API_URL + ARTWORK_SAVE_DOCUMENTS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getArtworkDto());
        assertEquals(2, response2.getArtworkDto().getDocuments().size());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void downloadArtworkDocument_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", 1L)
                .param("documentId", 1L)
                .when()
                .get(API_URL + ARTWORK_DOCUMENTS_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void downloadArtworkDocument_401_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoWithOwner();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", 1L)
                .when()
                .get(API_URL + ARTWORK_DOCUMENTS_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(401)
                .body(ERROR_CODE, is(ErrorCodes.User.ACCESS_NOT_ALLOWED.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void downloadArtworkDocument_404_notFound() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", -1L)
                .when()
                .get(API_URL + ARTWORK_DOCUMENTS_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void downloadArtworkDocument() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        var documentDto = artworkDto.getDocuments().stream().findFirst().get();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", documentDto.getId())
                .when()
                .get(API_URL + ARTWORK_DOCUMENTS_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void removeArtworkDocument_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", -1L)
                .param("documentId", -1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENTS_REMOVE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void removeArtworkDocument_401_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoWithOwner();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", 1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENTS_REMOVE)
                .then()
                .assertThat()
                .statusCode(401)
                .body(ERROR_CODE, is(ErrorCodes.User.ACCESS_NOT_ALLOWED.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void removeArtworkDocument_404_notFound() {
        var artworkDto = utilArtwork.savedArtworkDto();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", -1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENTS_REMOVE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void removeArtworkDocument() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        var documentDto = artworkDto.getDocuments().stream().findFirst().get();
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", documentDto.getId())
                .when()
                .delete(API_URL + ARTWORK_DOCUMENTS_REMOVE)
                .then()
                .assertThat()
                .statusCode(200);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkDto.getId())
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);

        var documents = response.getArtworkDto().getDocuments();
        assertTrue(documents.stream().noneMatch(documentDto1 -> documentDto1.getId().equals(documentDto.getId())));

    }
}
