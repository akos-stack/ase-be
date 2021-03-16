package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.artwork.UploadArtworkDocumentsResponse;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SetArtworkPrincipalImageRequest;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.*;
import static com.bloxico.ase.userservice.web.api.ArtworkDocumentsApi.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkDocumentsApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void downloadArtworkDocument_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", 1L)
                .param("documentId", 1L)
                .when()
                .get(API_URL + ARTWORK_DOCUMENT_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void downloadArtworkDocument_404_notFound() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId()));
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", -1L)
                .when()
                .get(API_URL + ARTWORK_DOCUMENT_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void downloadArtworkDocument() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        byte[] image = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParam("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UploadArtworkDocumentsResponse.class);
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", response.getDocuments().get(0).getId())
                .when()
                .get(API_URL + ARTWORK_DOCUMENT_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void uploadArtworkDocuments_403_notAuthorized() {
        var formParams = utilArtwork.genSaveArtworkDocumentsFormParams(IMAGE);
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments_404_notFound() {
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", -1L)
                .formParams("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments_404_notAllowed() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParams("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments_400_documentNotValid() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParams("fileCategory", CERTIFICATE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments_409_documentConflict() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
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
                    .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
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
                    .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                    .then()
                    .assertThat()
                    .statusCode(409)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_ALREADY_ATTACHED.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments_400_documentsSize() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
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
                    .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                    .then()
                    .assertThat()
                    .statusCode(400)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void uploadArtworkDocuments() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        byte[] image = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParam("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UploadArtworkDocumentsResponse.class);
        assertNotNull(response);
        assertNotNull(response.getDocuments());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void uploadArtworkDocumentsMng_403_notAuthorized() {
        var formParams = utilArtwork.genSaveArtworkDocumentsFormParams(IMAGE);
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void uploadArtworkDocumentsMng_404_notFound() {
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", -1L)
                .formParams("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void uploadArtworkDocumentsMng_400_documentNotValid() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        byte[] image = genFileBytes(IMAGE);
        given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParams("fileCategory", CERTIFICATE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void uploadArtworkDocumentsMng_409_documentConflict() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
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
                    .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
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
                    .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                    .then()
                    .assertThat()
                    .statusCode(409)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_ALREADY_ATTACHED.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void uploadArtworkDocumentsMng_400_documentsSize() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
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
                    .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                    .then()
                    .assertThat()
                    .statusCode(400)
                    .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.getCode()));
        }
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void uploadArtworkDocumentsMng() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        byte[] image = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParam("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UploadArtworkDocumentsResponse.class);
        assertNotNull(response);
        assertNotNull(response.getDocuments());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void deleteArtworkDocument_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", -1L)
                .param("documentId", -1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtworkDocument_404_notAllowed() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", 1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtworkDocument_404_notFound() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", -1L)
                .when()
                .delete(API_URL + ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtworkDocument() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        byte[] image = genFileBytes(IMAGE);
        var saveDocumentResponse = given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParam("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UploadArtworkDocumentsResponse.class);
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", saveDocumentResponse.getDocuments().get(0).getId())
                .when()
                .delete(API_URL + ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void deleteArtworkDocumentMng_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", -1L)
                .param("documentId", -1L)
                .when()
                .delete(API_URL + MNG_ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteArtworkDocumentMng_404_notFound() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", -1L)
                .when()
                .delete(API_URL + MNG_ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteArtworkDocumentMng() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        byte[] image = genFileBytes(IMAGE);
        var saveDocumentResponse = given()
                .header("Authorization", securityContext.getToken())
                .formParam("artworkId", artworkDto.getId())
                .formParam("fileCategory", IMAGE)
                .multiPart("documents", genUUID() + ".jpg", image)
                .when()
                .post(API_URL + MNG_ARTWORK_DOCUMENT_UPLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(UploadArtworkDocumentsResponse.class);
        given()
                .header("Authorization", securityContext.getToken())
                .param("artworkId", artworkDto.getId())
                .param("documentId", saveDocumentResponse.getDocuments().get(0).getId())
                .when()
                .delete(API_URL + MNG_ARTWORK_DOCUMENT_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void setPrincipalImg_403_notAuthorized(){
        var request = new SetArtworkPrincipalImageRequest(1L, 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void setPrincipalImg_403_admin_notAuthorized(){
        var request = new SetArtworkPrincipalImageRequest(1L, 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void setPrincipalImg_404_notFound(){
        var request = new SetArtworkPrincipalImageRequest(-1L, 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void setPrincipalImg_404_notAllowed(){
        var artwork = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void setPrincipalImg_404_artworkDocumentNotFound(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId()));
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), 1000L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void setPrincipalImg_404_documentIsNotImage(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId()));
        var document = utilArtwork.saveArtworkDocument(artwork.getId(), CERTIFICATE);
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), document.getId());
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_CANNOT_BE_PRINCIPAL_IMAGE.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void setPrincipalImg(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId()));
        var document = utilArtwork.saveArtworkDocument(artwork.getId(), IMAGE);
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), document.getId());
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void setPrincipalImgMng_403_notAuthorized(){
        var request = new SetArtworkPrincipalImageRequest(-1L, 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void setPrincipalImgMng_404_notFound(){
        var request = new SetArtworkPrincipalImageRequest(-1L, 1L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void setPrincipalImgMng_404_artworkDocumentNotFound(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), 1000L);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void setPrincipalImgMng_404_documentIsNotImage(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        var document = utilArtwork.saveArtworkDocument(artwork.getId(), CERTIFICATE);
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), document.getId());
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_DOCUMENT_CANNOT_BE_PRINCIPAL_IMAGE.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void setPrincipalImgMng(){
        var artwork = utilArtwork.saved(
                utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        var document = utilArtwork.saveArtworkDocument(artwork.getId(), IMAGE);
        var request = new SetArtworkPrincipalImageRequest(artwork.getId(), document.getId());
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_SET_PRINCIPAL_IMAGE)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
