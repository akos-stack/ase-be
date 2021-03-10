package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.repository.artwork.ArtworkGroupRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
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

    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private ArtworkRepository artworkRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private ArtworkGroupRepository artworkGroupRepository;

    @Test
    public void submitArtwork_notAuthorized() {
        var registration = utilAuth.doConfirmedRegistration();
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, null);
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
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
    public void submitArtwork_missingCertificate() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedArtOwnerDto(registration.getId());
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, null);
        formParams.put("iAmArtOwner", String.valueOf(false));
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artworks.ARTWORK_MISSING_CERTIFICATE.getCode()));
    }

    @Test
    public void submitArtwork_missingResume() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedArtOwnerDto(registration.getId());
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, null);
        formParams.put("iAmArtOwner", String.valueOf(true));
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.Artworks.ARTWORK_MISSING_RESUME.getCode()));
    }

    @Test
    public void submitArtwork_groupNotFound() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedArtOwnerDto(registration.getId());
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, -1L);
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParams(formParams)
                .multiPart("images", genUUID() + ".jpg", image)
                .multiPart("principalImage", genUUID() + ".jpg", image)
                .multiPart("document", genUUID() + ".txt", document)
                .when()
                .post(API_URL + SUBMIT_ARTWORK)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Artworks.ARTWORK_GROUP_NOT_FOUND.getCode()));
    }

    @Test
    public void submitArtwork_saveToNewGroup() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedArtOwnerDto(registration.getId());
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, null);
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
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
        assertNotNull(response.getGroupDto());
        assertSame(ArtworkGroup.Status.WAITING_FOR_EVALUATION, response.getGroupDto().getStatus());
    }

    @Test
    public void submitArtwork_saveToExistingGroup() {
        var registration = utilAuth.doConfirmedRegistration();
        utilUserProfile.savedArtOwnerDto(registration.getId());
        var groupDto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var formParams = utilArtwork.genSaveArtworkFormParams(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, groupDto.getId());
        byte[] image = genFileBytes(IMAGE);
        byte[] document = genFileBytes(CV);
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
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
        assertNotNull(response.getGroupDto());
        assertSame(ArtworkGroup.Status.WAITING_FOR_EVALUATION, response.getGroupDto().getStatus());
    }
}
