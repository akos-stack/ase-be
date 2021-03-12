package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.userservice.web.api.ArtworkApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void previewArtwork_401_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoWithOwner();
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkDto.getId())
                .when()
                .get(API_URL + ARTWORK_PREVIEW)
                .then()
                .assertThat()
                .statusCode(401)
                .body(ERROR_CODE, is(ErrorCodes.User.ACCESS_NOT_ALLOWED.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void previewArtwork_403_notAuthorized() {
        var artworkDto = utilArtwork.savedArtworkDtoWithOwner();
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .param("id", artworkDto.getId())
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
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
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
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
        assertEquals(artworkDto, response.getArtworkDto());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void saveArtworkDraft_notAuthorized() {
        given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .when()
                .post(API_URL + ARTWORK_SAVE_DRAFT)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkDraft() {
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .when()
                .post(API_URL + ARTWORK_SAVE_DRAFT)
                .then()
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto().getId());
        assertSame(Artwork.Status.DRAFT, response.getArtworkDto().getStatus());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void saveArtworkData() {
        var request = utilArtwork.genSaveArtworkDataRequest(Artwork.Status.DRAFT, true);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + ARTWORK_SAVE_DATA)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
    }

}
