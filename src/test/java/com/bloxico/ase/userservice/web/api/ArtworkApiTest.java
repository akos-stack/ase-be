package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.config.security.AseSecurityContext;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.DetailedArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkResponse;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.*;
import static com.bloxico.ase.userservice.util.FileCategory.PRINCIPAL_IMAGE;
import static com.bloxico.ase.userservice.web.api.ArtworkApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext securityContext;
    @Autowired private AseSecurityContext aseSecurityContext;
    @Autowired private UtilDocument utilDocument;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void createArtworkDraft_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .when()
                .post(API_URL + ARTWORK_CREATE_DRAFT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void createArtworkDraft() {
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .when()
                .post(API_URL + ARTWORK_CREATE_DRAFT)
                .then()
                .extract()
                .body()
                .as(ArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertSame(Artwork.Status.DRAFT, response.getArtwork().getStatus());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void previewArtwork_404_notAllowed() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT)).getId();
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkId)
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void previewArtwork_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", 1L)
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void previewArtwork_404_notFound() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", -1L)
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void previewArtwork() {
        var ownerId = aseSecurityContext.getArtOwnerId();
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT, ownerId)).getId();
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkId)
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(DetailedArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
        assertNotNull(response.getLocation());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void previewArtworkMng_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", 1L)
                .when()
                .get(API_URL + MNG_ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void previewArtworkMng_404_notFound() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", -1L)
                .when()
                .get(API_URL + MNG_ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void previewArtworkMng() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT)).getId();
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkId)
                .when()
                .get(API_URL + MNG_ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(DetailedArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
        assertNotNull(response.getLocation());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void updateArtwork_404_notAllowed() {
        var request = utilArtwork.genUpdateArtworkDataRequest(Artwork.Status.DRAFT);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void updateArtwork_403_notAuthorized() {
        var request = utilArtwork.genUpdateArtworkDataRequest(Artwork.Status.DRAFT);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void updateArtwork_404_artworkNotFound() {
        var request = utilArtwork.genUpdateArtworkDataRequest(-1L, Artwork.Status.DRAFT);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void updateArtwork_400_missingCertificate() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId())).getId();
        var documentId = utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId();
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION, false, documentId);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_CERTIFICATE.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void updateArtwork_400_missingResume() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId())).getId();
        var documentId = utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId();
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION, true, documentId);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_RESUME.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void updateArtwork() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT, securityContext.getLoggedInArtOwner().getId())).getId();
        utilArtwork.saveArtworkDocuments(artworkId);
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(DetailedArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void updateArtworkMng_403_notAuthorized() {
        var request = utilArtwork.genUpdateArtworkDataRequest(Artwork.Status.DRAFT);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateArtworkMng_404_artworkNotFound() {
        var request = utilArtwork.genUpdateArtworkDataRequest(-1L, Artwork.Status.DRAFT);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateArtworkMng_400_missingCertificate() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT)).getId();
        var documentId = utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId();
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION, false, documentId);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_CERTIFICATE.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateArtworkMng_400_missingResume() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT)).getId();
        var documentId = utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId();
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION, true, documentId);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_RESUME.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void updateArtworkMng() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT)).getId();
        utilArtwork.saveArtworkDocuments(artworkId);
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, Artwork.Status.READY_FOR_EVALUATION);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + MNG_ARTWORK_UPDATE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(DetailedArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void searchArtworks_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .params(allPages("status", Artwork.Status.DRAFT))
                .when()
                .get(API_URL + ARTWORK_SEARCH)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void searchArtworks_ofOwner() {
        var ownerId = securityContext.getLoggedInArtOwner().getId();
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork4 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION, ownerId));
        var artwork5 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION, ownerId));
        var artwork6 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT, ownerId));
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .params(allPages("status", DRAFT))
                .when()
                .get(API_URL + ARTWORK_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkResponse.class);
        assertThat(
                response.getPage().getContent(),
                Matchers.allOf(Matchers.hasItems(artwork6), Matchers.not(Matchers.hasItems(artwork1, artwork2, artwork3, artwork4, artwork5))));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void searchArtworks() {
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork4 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork5 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork6 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .params(allPages("status", DRAFT))
                .when()
                .get(API_URL + MNG_ARTWORK_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchArtworkResponse.class);
        assertThat(
                response.getPage().getContent(),
                Matchers.allOf(Matchers.hasItems(artwork1, artwork6), Matchers.not(Matchers.hasItems(artwork2, artwork3, artwork4, artwork5))));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtwork_404_notAllowed() {
        var request = utilArtwork.saved(utilArtwork.genArtworkDto());
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", request.getId())
                .when()
                .delete(API_URL + ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void deleteArtwork_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", 1L)
                .when()
                .delete(API_URL + ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtwork_404_notFound() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", -1L)
                .when()
                .delete(API_URL + ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void deleteArtwork() {
        var request = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT,
                securityContext.getLoggedInArtOwner().getId()));
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", request.getId())
                .when()
                .delete(API_URL + ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", request.getId())
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void deleteArtworkMng_403_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", 1L)
                .when()
                .delete(API_URL + MNG_ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteArtworkMng_404_notFound() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", -1L)
                .when()
                .delete(API_URL + MNG_ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void deleteArtworkMng() {
        var request = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("artworkId", request.getId())
                .when()
                .delete(API_URL + MNG_ARTWORK_DELETE)
                .then()
                .assertThat()
                .statusCode(200);
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", request.getId())
                .when()
                .get(API_URL + MNG_ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(404);
    }

}
