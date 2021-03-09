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

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.web.api.ArtworkApi.SUBMIT_ARTWORK;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class ArtworkApiTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void submitArtwork_notAuthorized() {
        var formParams = utilArtwork.genSaveArtworkFormParams(Artwork.Status.WAITING_FOR_EVALUATION, false);
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void submitArtwork_missingCertificate() {
        var formParams = utilArtwork.genSaveArtworkFormParams(Artwork.Status.WAITING_FOR_EVALUATION, true);
        formParams.put("iAmArtOwner", String.valueOf(false));
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_CERTIFICATE.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void submitArtwork_missingResume() {
        var formParams = utilArtwork.genSaveArtworkFormParams(Artwork.Status.WAITING_FOR_EVALUATION, false);
        formParams.put("iAmArtOwner", String.valueOf(true));
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artwork.ARTWORK_MISSING_RESUME.getCode()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER, auth = true)
    public void submitArtwork() {
        var formParams = utilArtwork.genSaveArtworkFormParams(Artwork.Status.WAITING_FOR_EVALUATION, false);
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        var response = given()
                .header("Authorization", securityContext.getToken())
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveArtworkResponse.class);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
        assertSame(Artwork.Status.WAITING_FOR_EVALUATION, response.getArtworkDto().getStatus());
    }
}
