package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.util.FileCategory.CERTIFICATE;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.genFileBytes;
import static com.bloxico.ase.userservice.web.api.S3Api.S3_INVALID_FILES;

@Transactional(propagation = NOT_SUPPORTED)
public class S3ApiTest extends AbstractSpringTestWithAWS{

    @Autowired
    private UtilAuth utilAuth;

    @Test
    public void invalidFiles_ok_FileCategory_CV() {
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "CV")
                .multiPart("files",genUUID() + ".txt", genFileBytes(CV))
                .multiPart("files",genUUID() + ".txt", genFileBytes(CV))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertTrue(response.getErrors().isEmpty());
        assertEquals(0, response.getErrors().size());
    }

    @Test
    public void invalidFiles_FileCategory_IMAGE() {
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertTrue(response.getErrors().isEmpty());
        assertEquals(0, response.getErrors().size());
    }

    @Test
    public void invalidFiles_file_type_not_supported() {
        var textFile = genUUID() + ".txt";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .multiPart("files",  textFile, genFileBytes(CV))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(textFile);
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY), errors);
    }

    @Test
    public void invalidFiles_file_certificate_size_exceeded() {
        var textFile = genUUID() + ".pdf";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "CERTIFICATE")
                .multiPart("files",  textFile, genInvalidFileBytes(CERTIFICATE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(textFile);
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED), errors);
    }

    @Test
    public void invalidFiles_file_cv_size_exceeded() {
        var textFile = genUUID() + ".pdf";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "CV")
                .multiPart("files",  genUUID() + ".txt", genFileBytes(CV))
                .multiPart("files",  textFile, genInvalidFileBytes(CV))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(textFile);
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED), errors);
    }

    @Test
    public void invalidFiles_all_errors() {
        var textFile = genUUID() + ".png";
        var textFile2 = genUUID() + ".png";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "CV")
                .multiPart("files",  textFile, genInvalidFileBytes(IMAGE))
                .multiPart("files",  textFile2, genInvalidFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(2, response.getErrors().size());
        var errors = response.getErrors().get(textFile);
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED,
                ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY), errors);
    }

    @Test
    public void invalidFiles_file_image_size_exceeded() {
        var imgFile = genUUID() + ".png";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  imgFile, genInvalidFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(imgFile);
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED), errors);
    }

    @Test
    public void invalidFiles_wrong_file_category() {
        var imageFile = genUUID() + ".jpg";
        var imageFile2 = genUUID() + ".jpg";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "CV")
                .multiPart("files",  imageFile, genFileBytes(IMAGE))
                .multiPart("files",  imageFile2, genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(2, response.getErrors().size());
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.getErrors().get(imageFile));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.getErrors().get(imageFile2));
    }

    @Test
    public void invalidFiles_ok_one_file_sended() {
        var imageFile = genUUID() + ".jpg";
        var response = given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  imageFile, genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_INVALID_FILES)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(ValidateFilesResponse.class);
        assertTrue(response.getErrors().isEmpty());
        assertEquals(0, response.getErrors().size());
    }
}
